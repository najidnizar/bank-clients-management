package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerEntity toEntity(CreateCustomerRequest request);

}
