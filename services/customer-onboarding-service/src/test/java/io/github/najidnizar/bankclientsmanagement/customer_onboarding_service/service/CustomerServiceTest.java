package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerResponse;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.EmailAlreadyExistsException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper.CustomerMapper;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    public static final String CUSTOMER_NUMBER = "C000000001";
    public static final String FIRST_NAME = "Nizar";
    public static final String LAST_NAME = "Najid";
    public static final String EMAIL = "nizar@test.com";
    public static final String PHONE_NUMBER = "+352621234567";

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerSuccessfully() {

        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(1995, 11, 26))
                .email(EMAIL)
                .phoneNumber(PHONE_NUMBER)
                .build();

        CustomerEntity savedCustomer = new CustomerEntity();
        savedCustomer.setId(UUID.randomUUID());
        savedCustomer.setCustomerNumber(CUSTOMER_NUMBER);

        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName(FIRST_NAME);
        customer.setLastName(LAST_NAME);
        customer.setDateOfBirth(LocalDate.of(1995, 11, 26));
        customer.setEmail(EMAIL);
        customer.setPhoneNumber(PHONE_NUMBER);

        CreateCustomerResponse response = CreateCustomerResponse.builder()
                .id(savedCustomer.getId())
                .customerNumber(savedCustomer.getCustomerNumber())
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .build();

        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.getNextCustomerNumber()).thenReturn(1L);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerMapper.toResponse(savedCustomer)).thenReturn(response);

        CreateCustomerResponse result = customerService.createCustomer(request);

        assertNotNull(result);
        assertEquals(savedCustomer.getId(), result.id());
        assertEquals(CUSTOMER_NUMBER, result.customerNumber());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(EMAIL, result.email());

        verify(customerRepository).existsByEmail(request.email());
        verify(customerMapper).toEntity(request);
        verify(customerRepository).getNextCustomerNumber();
        verify(customerMapper).toResponse(savedCustomer);

        // testing the argument of repository call
        ArgumentCaptor<CustomerEntity> customerCaptor =
                ArgumentCaptor.forClass(CustomerEntity.class);

        verify(customerRepository).save(customerCaptor.capture());

        CustomerEntity capturedCustomer = customerCaptor.getValue();

        assertEquals(CUSTOMER_NUMBER, capturedCustomer.getCustomerNumber());
        assertEquals(FIRST_NAME, capturedCustomer.getFirstName());
        assertEquals(LAST_NAME, capturedCustomer.getLastName());
        assertEquals(LocalDate.of(1995, 11, 26), capturedCustomer.getDateOfBirth());
        assertEquals(EMAIL, capturedCustomer.getEmail());
        assertEquals(PHONE_NUMBER, capturedCustomer.getPhoneNumber());
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailAlreadyExists() {
        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber(PHONE_NUMBER)
                .build();

        when(customerRepository.existsByEmail(request.email())).thenReturn(true);
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> customerService.createCustomer(request));

        assertEquals(
                "Email 'nizar@test.com' already exists.",
                exception.getMessage()
        );

        verify(customerRepository).existsByEmail(request.email());
        verify(customerRepository, never()).getNextCustomerNumber();
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toEntity(any());
        verify(customerMapper, never()).toResponse(any());
    }

    @Test
    void shouldReturnCustomerWhenIdExists() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        CustomerEntity customer = CustomerEntity.builder()
                .id(customerId)
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();

        CreateCustomerResponse response = CreateCustomerResponse.builder()
                .id(customerId)
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .email("nizar@test.com")
                .build();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toCreateCustomerResponse(customer))
                .thenReturn(response);

        // Act
        CreateCustomerResponse result = customerService.getCustomerById(customerId);

        // Assert
        assertEquals(customerId, result.id());
        assertEquals("C000000001", result.customerNumber());

        verify(customerRepository).findById(customerId);
        verify(customerMapper).toCreateCustomerResponse(customer);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(customerId)
        );

        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).toCreateCustomerResponse(any());
    }

}