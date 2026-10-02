package io.github.najidnizar.bankclientsmanagement.customer_account_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.domain.AccountStatus;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.dto.AccountResponse;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.dto.AccountStatusHistoryResponse;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.dto.CreateAccountRequest;
import io.github.najidnizar.bankclientsmanagement.customer_account_service.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping("/ping")
    public String ping() {
        return "Customer Account Service Running";
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bankAccountService.createAccount(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(bankAccountService.getAccountById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<AccountResponse>> getAccountsByCustomerId(
            @PathVariable UUID customerId,
            Pageable pageable) {
        return ResponseEntity.ok(bankAccountService.getAccountsByCustomerId(customerId, pageable)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(@PathVariable UUID id, @RequestParam AccountStatus status) {
        return ResponseEntity.ok(bankAccountService.updateAccountStatus(id, status));
    }

    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<AccountStatusHistoryResponse>> getAccountStatusHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(bankAccountService.getAccountStatusHistory(id));
    }
}
