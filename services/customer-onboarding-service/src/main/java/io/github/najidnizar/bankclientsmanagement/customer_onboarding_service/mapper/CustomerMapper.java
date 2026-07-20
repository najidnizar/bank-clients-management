package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerResponse;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.UpdateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerEntity toEntity(CreateCustomerRequest request);

    CustomerResponse toResponse(CustomerEntity customer);

    CustomerResponse toCustomerResponse(CustomerEntity customer);

    void updateCustomer(UpdateCustomerRequest request, @MappingTarget CustomerEntity customer);
}
