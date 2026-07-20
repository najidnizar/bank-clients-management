package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config.AbstractPostgresIntegrationTest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldFindCustomerByCustomerNumber() {
        // Arrange
        CustomerEntity customer = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();

        customerRepository.save(customer);

        // Act
        Optional<CustomerEntity> result =
                customerRepository.findByCustomerNumber("C000000001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("C000000001", result.get().getCustomerNumber());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        CustomerEntity customer = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();

        customerRepository.save(customer);
        boolean exists = customerRepository.existsByEmail("nizar@test.com");
        assertTrue(exists);

    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Act
        boolean exists = customerRepository.existsByEmail("unknown@test.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    void shouldFindCustomerByEmail() {
        // Arrange
        CustomerEntity customer = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();

        customerRepository.save(customer);

        // Act
        Optional<CustomerEntity> result =
                customerRepository.findByEmail("nizar@test.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Nizar", result.get().getFirstName());
        assertEquals("Najid", result.get().getLastName());
        assertEquals("nizar@test.com", result.get().getEmail());
    }

    @Test
    void shouldReturnTrueWhenCustomerNumberExists() {
        // Arrange
        CustomerEntity customer = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .build();

        customerRepository.save(customer);

        // Act
        boolean exists =
                customerRepository.existsByCustomerNumber("C000000001");

        // Assert
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenCustomerNumberDoesNotExist() {
        // Act
        boolean exists =
                customerRepository.existsByCustomerNumber("C999999999");

        // Assert
        assertFalse(exists);
    }

    @Test
    void shouldReturnCustomersPageByFirstName() {
        Pageable pageable = PageRequest.of(0, 10);

        CustomerEntity john = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000001")
                        .firstName("John")
                        .lastName("Smith")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("John@test.com")
                        .phoneNumber("+352621234567")
                .build());

        CustomerEntity johnny = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000002")
                        .firstName("Johnny")
                        .lastName("Doe")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Johnny@test.com")
                        .phoneNumber("+352621234567")
                .build());

        CustomerEntity alice = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000003")
                        .firstName("Alice")
                        .lastName("Brown")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Alice@test.com")
                        .phoneNumber("+352621234567")
                .build());

        Page<CustomerEntity> result =
                customerRepository.findByFirstNameContainingIgnoreCase(
                        "john",
                        pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(
                result.getContent()
                        .stream()
                        .allMatch(c ->
                                c.getFirstName().toLowerCase().contains("john")));
    }

    @Test
    void shouldReturnEmptyPageWhenFirstNameNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        CustomerEntity john = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000001")
                        .firstName("John")
                        .lastName("Smith")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("John@test.com")
                        .phoneNumber("+352621234567")
                        .build());

        CustomerEntity johnny = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000002")
                        .firstName("Johnny")
                        .lastName("Doe")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Johnny@test.com")
                        .phoneNumber("+352621234567")
                        .build());

        CustomerEntity alice = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000003")
                        .firstName("Alice")
                        .lastName("Brown")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Alice@test.com")
                        .phoneNumber("+352621234567")
                        .build());

        Page<CustomerEntity> result =
                customerRepository.findByFirstNameContainingIgnoreCase(
                        "xyz",
                        pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

}