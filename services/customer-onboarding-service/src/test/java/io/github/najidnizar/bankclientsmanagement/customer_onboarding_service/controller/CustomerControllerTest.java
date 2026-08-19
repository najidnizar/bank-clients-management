package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.controller;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CreateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerResponse;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.UpdateCustomerRequest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.CustomerNotFoundException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.EmailAlreadyExistsException;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.exception.GlobalExceptionHandler;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {

        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();

        CustomerResponse response = CustomerResponse.builder()
                .id(UUID.randomUUID())
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .email("nizar@test.com")
                .build();

        when(customerService.createCustomer(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerNumber").value("C000000001"))
                .andExpect(jsonPath("$.firstName").value("Nizar"))
                .andExpect(jsonPath("$.lastName").value("Najid"))
                .andExpect(jsonPath("$.email").value("nizar@test.com"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .firstName("")
                .lastName("")
                .dateOfBirth(null)
                .email("invalid-email")
                .phoneNumber("")
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/customers"));

        verify(customerService, never()).createCustomer(any());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();

        when(customerService.createCustomer(any()))
                .thenThrow(new EmailAlreadyExistsException(request.email()));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/customers"));

        verify(customerService).createCustomer(any());
    }

    @Test
    void shouldReturnCustomerWhenIdExists() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();

        CustomerResponse response = CustomerResponse.builder()
                .id(customerId)
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .email("nizar@test.com")
                .build();

        when(customerService.getCustomerById(customerId))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.customerNumber").value("C000000001"))
                .andExpect(jsonPath("$.firstName").value("Nizar"))
                .andExpect(jsonPath("$.lastName").value("Najid"))
                .andExpect(jsonPath("$.email").value("nizar@test.com"));

        verify(customerService).getCustomerById(customerId);
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();

        when(customerService.getCustomerById(customerId))
                .thenThrow(new CustomerNotFoundException(customerId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Customer not found with id: " + customerId))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/customers/" + customerId));

        verify(customerService).getCustomerById(customerId);
    }

    @Test
    void shouldReturnCustomersPage() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerResponse response = CustomerResponse.builder()
                .id(customerId)
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .email("nizar@test.com")
                .build();
        Page<CustomerResponse> page = new PageImpl<>(List.of(response));
        when(customerService.getCustomers(any(Pageable.class))).thenReturn(page);
        mockMvc.perform(
                        get("/api/v1/customers")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].firstName")
                        .value("Nizar"))
                .andExpect(jsonPath("$.totalElements")
                        .value(1));
        verify(customerService).getCustomers(any(Pageable.class));
    }

//    @Test
//    void shouldReturnCustomersPageByFirstName() throws Exception {
//        UUID customerId = UUID.randomUUID();
//        CustomerResponse response = CustomerResponse.builder()
//                .id(customerId)
//                .customerNumber("C000000001")
//                .firstName("Nizar")
//                .lastName("Najid")
//                .email("nizar@test.com")
//                .build();
//        Page<CustomerResponse> page = new PageImpl<>(List.of(response));
//        when(customerService.searchCustomersByFirstName(anyString(), any(Pageable.class))).thenReturn(page);
//        mockMvc.perform(
//                        get("/api/v1/customers/search")
//                                .param("firstName", "Nizar")
//                                .param("page", "0")
//                                .param("size", "10")
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content[0].firstName")
//                        .value("Nizar"))
//                .andExpect(jsonPath("$.totalElements")
//                        .value(1));
//        verify(customerService).searchCustomersByFirstName(anyString(), any(Pageable.class));
//    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerResponse response = CustomerResponse.builder()
                .id(customerId)
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .email("nizar@test.com")
                .build();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();
        when(customerService.updateCustomer(eq(customerId), any(UpdateCustomerRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/customers/{id}", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.firstName").value("Nizar"))
                .andExpect(jsonPath("$.lastName").value("Najid"))
                .andExpect(jsonPath("$.email").value("nizar@test.com"));

        verify(customerService).updateCustomer(eq(customerId), eq(request));
    }

    @Test
    void shouldReturnCustomerNotFound() throws Exception {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();
        when(customerService.updateCustomer(eq(customerId), any(UpdateCustomerRequest.class)))
                .thenThrow(new CustomerNotFoundException(customerId));

        mockMvc.perform(
                        put("/api/v1/customers/{id}", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowDuplicateEmailException() throws Exception {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();
        when(customerService.updateCustomer(customerId, request)).thenThrow(new EmailAlreadyExistsException("nizar@test.com"));

        mockMvc.perform(
                        put("/api/v1/customers/{id}", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("")
                .lastName("")
                .email("abc")
                .phoneNumber("123")
                .build();

        mockMvc.perform(
                        put("/api/v1/customers/{id}", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void shouldCloseCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(customerService).closeCustomer(id);
        mockMvc.perform(post("/api/v1/customers/{id}/close", id)).andExpect(status().isNoContent());
        verify(customerService).closeCustomer(id);
    }

    @Test
    void shouldReturnNotFoundWhenClosingUnknownCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new CustomerNotFoundException(id)).when(customerService).closeCustomer(id);
        mockMvc.perform(post("/api/v1/customers/{id}/close", id)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenOptimisticLockOccurs() throws Exception {

        UUID id = UUID.randomUUID();
        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();
        when(customerService.updateCustomer(eq(id), any(UpdateCustomerRequest.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException("CustomerEntity", id));

        mockMvc.perform(
                        put("/api/v1/customers/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

}