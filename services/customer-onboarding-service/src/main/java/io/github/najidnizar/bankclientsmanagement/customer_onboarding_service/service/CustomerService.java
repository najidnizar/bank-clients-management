package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerResponse;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerSearchCriteria;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.UpdateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatusHistoryEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.event.CustomerEvent;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.event.CustomerEventPublisher;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.event.CustomerEventType;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.EmailAlreadyExistsException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.InvalidCustomerStatusTransitionException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper.CustomerMapper;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerRepository;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerStatusHistoryRepository;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.specification.CustomerSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerStatusHistoryRepository customerStatusHistoryRepository;
    private final CustomerMapper customerMapper;
    private final CustomerEventPublisher customerEventPublisher;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        CustomerEntity customer = customerMapper.toEntity(request);
        customer.setStatus(CustomerStatus.ACTIVE);
        Long nextNumber = customerRepository.getNextCustomerNumber();
        customer.setCustomerNumber(String.format("C%09d", nextNumber));
        CustomerEntity savedCustomer = customerRepository.save(customer);

        customerEventPublisher.publishCustomerCreated(
                new CustomerEvent(
                        CustomerEventType.CUSTOMER_CREATED,
                        savedCustomer.getId(),
                        savedCustomer.getCustomerNumber(),
                        savedCustomer.getStatus()
                )
        );

        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional
    public void closeCustomer(UUID id) {
        updateCustomerStatus(id, CustomerStatus.CLOSED);
    }

    @Transactional
    public void blockCustomer(UUID id) {
        updateCustomerStatus(id, CustomerStatus.BLOCKED);
    }

    @Transactional
    public void unblockCustomer(UUID id) {
        updateCustomerStatus(id, CustomerStatus.ACTIVE);
    }

    public CustomerResponse getCustomerById(UUID id) {

        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toResponse(customer);
    }

    public CustomerResponse getCustomerByCustomerNumber(String customerNumber) {

        CustomerEntity customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerNumber));

        return customerMapper.toResponse(customer);
    }

    public Page<CustomerResponse> getCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toResponse);
    }

    public Page<CustomerResponse> searchCustomers(CustomerSearchCriteria criteria, Pageable pageable) {
        return customerRepository
                .findAll(CustomerSpecification.build(criteria), pageable)
                .map(customerMapper::toResponse);
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        if (!customer.getEmail().equals(request.email()) && customerRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        customerMapper.updateCustomer(request, customer);
        CustomerEntity updated = customerRepository.save(customer);
        return customerMapper.toResponse(updated);
    }

    private void updateCustomerStatus(UUID id, CustomerStatus status) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        CustomerStatus oldStatus = customer.getStatus();

        if (oldStatus == CustomerStatus.CLOSED) {
            throw new InvalidCustomerStatusTransitionException(customer.getStatus(), status);
        }
        customer.setStatus(status);
        customerRepository.save(customer);

        customerEventPublisher.publishCustomerStatusChanged(
                new CustomerEvent(
                        CustomerEventType.CUSTOMER_STATUS_CHANGED,
                        customer.getId(),
                        customer.getCustomerNumber(),
                        customer.getStatus()
                )
        );

        CustomerStatusHistoryEntity history =
                CustomerStatusHistoryEntity.builder()
                        .id(UUID.randomUUID())
                        .customer(customer)
                        .oldStatus(oldStatus)
                        .newStatus(status)
                        .changedAt(LocalDateTime.now())
                        .changedBy("SYSTEM")
                        .build();

        customerStatusHistoryRepository.save(history);
    }
}
