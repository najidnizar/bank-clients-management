package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper.CustomerMapper;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerEntity createCustomer(CreateCustomerRequest request) {
        CustomerEntity customer = customerMapper.toEntity(request);
        Long nextNumber = customerRepository.getNextCustomerNumber();
        customer.setCustomerNumber(String.format("C%09d", nextNumber));
        return customerRepository.save(customer);
    }

}
