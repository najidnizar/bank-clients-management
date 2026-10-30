package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerResponse;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.UpdateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.EmailAlreadyExistsException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.InvalidCustomerStatusTransitionException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.mapper.CustomerMapper;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerRepository;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository.CustomerStatusHistoryRepository;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.event.CustomerEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
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
    private CustomerStatusHistoryRepository customerStatusHistoryRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerEventPublisher customerEventPublisher;

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

        CustomerResponse response = CustomerResponse.builder()
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

        CustomerResponse result = customerService.createCustomer(request);

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

        CustomerResponse response = CustomerResponse.builder()
                .id(customerId)
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .email("nizar@test.com")
                .build();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        // Act
        CustomerResponse result = customerService.getCustomerById(customerId);

        // Assert
        assertEquals(customerId, result.id());
        assertEquals("C000000001", result.customerNumber());

        verify(customerRepository).findById(customerId);
        verify(customerMapper).toResponse(customer);
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
        verify(customerMapper, never()).toResponse(any());
    }

    @Test
    void shouldReturnCustomersPage() {
        Pageable pageable = PageRequest.of(0, 10);

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

        CustomerResponse response = CustomerResponse.builder()
                .id(customerId)
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .email("nizar@test.com")
                .build();

        Page<CustomerEntity> page = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        Page<CustomerResponse> result = customerService.getCustomers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Nizar", result.getContent().getFirst().firstName());
        verify(customerRepository).findAll(pageable);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldReturnCustomersEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomerEntity> emptyPage = Page.empty(pageable);

        when(customerRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<CustomerResponse> result = customerService.getCustomers(pageable);

        assertTrue(result.isEmpty());
        verify(customerRepository).findAll(pageable);
        verify(customerMapper, never()).toResponse(any());
    }

    @Test
    void shouldUpdateCustomer() {
        UUID id = UUID.randomUUID();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .phoneNumber("+352661123456")
                .build();

        CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .firstName("Old")
                .lastName("Name")
                .email("old@test.com")
                .phoneNumber("+352111111111")
                .build();

        CustomerResponse response = CustomerResponse.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .phoneNumber("+352661123456")
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.updateCustomer(id, request);

        assertEquals(id, result.id());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());

        verify(customerRepository).findById(id);
        verify(customerRepository).existsByEmail("john@test.com");
        verify(customerMapper).updateCustomer(request, customer);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldThrowCustomerNotFound() {
        UUID id = UUID.randomUUID();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .phoneNumber("+352661123456")
                .build();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(id, request));

        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).updateCustomer(any(), any());
    }

    @Test
    void shouldThrowDuplicateEmail() {
        UUID id = UUID.randomUUID();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .phoneNumber("+352661123456")
                .build();

        CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .firstName("Old")
                .lastName("Name")
                .email("old@test.com")
                .phoneNumber("+352111111111")
                .build();

        CustomerResponse response = CustomerResponse.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .phoneNumber("+352661123456")
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> customerService.updateCustomer(id, request));

        verify(customerRepository).findById(id);
        verify(customerRepository).existsByEmail("john@test.com");
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).updateCustomer(any(), any());
    }

    @Test
    void shouldCloseCustomer() {
        UUID id = UUID.randomUUID();
        CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .status(CustomerStatus.ACTIVE)
                .firstName("Old")
                .lastName("Name")
                .email("old@test.com")
                .phoneNumber("+352111111111")
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        customerService.closeCustomer(id);

        assertEquals(CustomerStatus.CLOSED, customer.getStatus());
        verify(customerRepository).findById(id);
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenClosingAndCustomerDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.closeCustomer(id));
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldBlockCustomer() {
        UUID id = UUID.randomUUID();
        CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .status(CustomerStatus.ACTIVE)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        customerService.blockCustomer(id);

        assertEquals(CustomerStatus.BLOCKED, customer.getStatus());
        verify(customerRepository).findById(id);
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenBlockCustomerDoesNotExist(){
        // Arrange
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> customerService.blockCustomer(customerId));

        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).toResponse(any());
    }

    @Test
    void shouldThrowExceptionWhenBlockingClosedCustomer() {
        UUID id = UUID.randomUUID();
        CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .status(CustomerStatus.CLOSED)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        assertThrows(InvalidCustomerStatusTransitionException.class, () -> customerService.blockCustomer(id));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUnblockingClosedCustomer() {
        UUID id = UUID.randomUUID();
        CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .status(CustomerStatus.CLOSED)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        assertThrows(InvalidCustomerStatusTransitionException.class, () -> customerService.unblockCustomer(id));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldCreateStatusHistoryWhenBlockingCustomer() {
        // Arrange
        UUID id = UUID.randomUUID();

        CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .status(CustomerStatus.ACTIVE)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // Act
        customerService.blockCustomer(id);

        // Assert
        assertEquals(CustomerStatus.BLOCKED, customer.getStatus());

        verify(customerRepository).save(customer);

        verify(customerStatusHistoryRepository).save(
                argThat(history ->
                        history.getCustomer().equals(customer)
                                && history.getOldStatus() == CustomerStatus.ACTIVE
                                && history.getNewStatus() == CustomerStatus.BLOCKED
                                && history.getChangedBy().equals("SYSTEM")
                                && history.getChangedAt() != null
                )
        );
    }

}