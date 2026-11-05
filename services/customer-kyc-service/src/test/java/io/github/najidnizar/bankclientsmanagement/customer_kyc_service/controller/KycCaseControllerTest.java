package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.config.SecurityConfig;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.CreateKycCaseRequest;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.KycCaseResponse;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycRiskLevel;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.exception.InvalidKycStatusTransitionException;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.service.KycCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// SecurityConfig is imported so that the real security rules (@PreAuthorize) are applied in this slice test
@WebMvcTest(KycCaseController.class)
@Import(SecurityConfig.class)
class KycCaseControllerTest {

    private static final SimpleGrantedAuthority COMPLIANCE_OFFICER = new SimpleGrantedAuthority("ROLE_COMPLIANCE_OFFICER");
    private static final SimpleGrantedAuthority ADVISOR = new SimpleGrantedAuthority("ROLE_ADVISOR");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KycCaseService kycCaseService;

    // Required by the resource server configuration; never called because jwt() injects an already authenticated token
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldCreateKycCase() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID kycCaseId = UUID.randomUUID();

        CreateKycCaseRequest request = new CreateKycCaseRequest(customerId, KycRiskLevel.LOW, 20);

        KycCaseResponse response = new KycCaseResponse(
                kycCaseId,
                customerId,
                KycCaseStatus.NOT_STARTED,
                KycRiskLevel.LOW,
                20,
                LocalDateTime.now(),
                "SYSTEM",
                LocalDateTime.now(),
                "SYSTEM"
        );

        when(kycCaseService.createKycCase(request))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/kyc-cases")
                                .with(jwt().authorities(COMPLIANCE_OFFICER))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(kycCaseId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"))
                .andExpect(jsonPath("$.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.riskScore").value(20));
    }

    @Test
    void shouldReturnConflictWhenKycStatusTransitionIsInvalid() throws Exception {

        UUID id = UUID.randomUUID();

        when(kycCaseService.updateKycStatus(id, KycCaseStatus.IN_PROGRESS)).thenThrow(
                        new InvalidKycStatusTransitionException(
                                KycCaseStatus.APPROVED,
                                KycCaseStatus.IN_PROGRESS
                        )
                );

        mockMvc.perform(patch("/api/v1/kyc-cases/{id}/status", id)
                        .param("status", "IN_PROGRESS")
                        .with(jwt().authorities(COMPLIANCE_OFFICER)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid KYC status transition from APPROVED to IN_PROGRESS"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoTokenIsSent() throws Exception {
        mockMvc.perform(patch("/api/v1/kyc-cases/{id}/status", UUID.randomUUID())
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(kycCaseService);
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotComplianceOfficer() throws Exception {
        mockMvc.perform(patch("/api/v1/kyc-cases/{id}/status", UUID.randomUUID())
                        .param("status", "IN_PROGRESS")
                        .with(jwt().authorities(ADVISOR)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(kycCaseService);
    }
}
