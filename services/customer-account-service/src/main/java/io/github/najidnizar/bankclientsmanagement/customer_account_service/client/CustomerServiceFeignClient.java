package io.github.najidnizar.bankclientsmanagement.customer_account_service.client;


import io.github.najidnizar.bankclientsmanagement.customer_account_service.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-onboarding-service")
public interface CustomerServiceFeignClient {

    @GetMapping("/api/v1/customers/{id}")
    ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id);
}
