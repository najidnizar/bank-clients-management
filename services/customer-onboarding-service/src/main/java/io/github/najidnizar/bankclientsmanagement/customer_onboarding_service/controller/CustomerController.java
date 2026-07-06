package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/ping")
    public String ping() {
        return "Customer Onboarding Service Running";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerEntity createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }

}
