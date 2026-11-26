package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.CreateKycCaseRequest;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.dto.KycCaseResponse;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.entity.KycCaseStatus;
import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.service.KycCaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kyc-cases")
@Tag(name = "KycCases", description = "KYC case management APIs")
@RequiredArgsConstructor
public class KycCaseController {

    private final KycCaseService kycCaseService;

    @PostMapping
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<KycCaseResponse> createKycCase(@Valid @RequestBody CreateKycCaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kycCaseService.createKycCase(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KycCaseResponse> getKycCaseById(@PathVariable UUID id) {
        return ResponseEntity.ok(kycCaseService.getKycCaseById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<KycCaseResponse> getKycCaseByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(kycCaseService.getKycCaseByCustomerId(customerId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<KycCaseResponse> updateKycStatus(@PathVariable UUID id, @RequestParam KycCaseStatus status) {
        return ResponseEntity.ok(kycCaseService.updateKycStatus(id, status));
    }
}
