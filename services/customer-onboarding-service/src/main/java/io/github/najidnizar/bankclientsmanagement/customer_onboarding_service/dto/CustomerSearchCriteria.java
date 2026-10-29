package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;
import lombok.Builder;

@Builder
public record CustomerSearchCriteria(

        String firstName,

        String lastName,

        String email,

        String customerNumber,

        CustomerStatus status

) {
}