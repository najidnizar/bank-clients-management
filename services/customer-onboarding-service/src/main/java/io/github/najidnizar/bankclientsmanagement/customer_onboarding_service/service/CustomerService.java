package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerResponse;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.UpdateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.EmailAlreadyExistsException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper.CustomerMapper;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        CustomerEntity customer = customerMapper.toEntity(request);
        Long nextNumber = customerRepository.getNextCustomerNumber();
        customer.setCustomerNumber(String.format("C%09d", nextNumber));
        CustomerEntity savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    public CustomerResponse getCustomerById(UUID id) {

        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toCustomerResponse(customer);
    }

    public CustomerResponse getCustomerByCustomerNumber(String customerNumber) {

        CustomerEntity customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerNumber));

        return customerMapper.toCustomerResponse(customer);
    }

    public Page<CustomerResponse> getCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toCustomerResponse);
    }

    public Page<CustomerResponse> searchCustomersByFirstName(String firstName, Pageable pageable) {
        return customerRepository
                .findByFirstNameContainingIgnoreCase(firstName, pageable)
                .map(customerMapper::toCustomerResponse);
    }

    public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        if (!customer.getEmail().equals(request.email()) && customerRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        customerMapper.updateCustomer(request, customer);
        CustomerEntity updated = customerRepository.save(customer);
        return customerMapper.toCustomerResponse(updated);
    }

}
