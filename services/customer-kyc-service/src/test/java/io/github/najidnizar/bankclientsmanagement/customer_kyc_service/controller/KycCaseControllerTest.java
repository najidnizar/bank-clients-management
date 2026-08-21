package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.CreateKycCaseRequest;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.KycCaseResponse;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycRiskLevel;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.exception.InvalidKycStatusTransitionException;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.service.KycCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(KycCaseController.class)
class KycCaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KycCaseService kycCaseService;

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

        mockMvc.perform(patch("/api/v1/kyc-cases/{id}/status", id).param("status", "IN_PROGRESS"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid KYC status transition from APPROVED to IN_PROGRESS"));
    }



}























