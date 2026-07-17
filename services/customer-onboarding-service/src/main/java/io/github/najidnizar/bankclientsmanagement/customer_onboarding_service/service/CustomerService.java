package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerResponse;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.EmailAlreadyExistsException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper.CustomerMapper;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        CustomerEntity customer = customerMapper.toEntity(request);
        Long nextNumber = customerRepository.getNextCustomerNumber();
        customer.setCustomerNumber(String.format("C%09d", nextNumber));
        CustomerEntity savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    public CreateCustomerResponse getCustomerById(UUID id) {

        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toCreateCustomerResponse(customer);
    }

}
