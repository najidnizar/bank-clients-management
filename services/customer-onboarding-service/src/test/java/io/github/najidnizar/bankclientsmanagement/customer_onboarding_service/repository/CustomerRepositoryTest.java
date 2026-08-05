package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config.AbstractPostgresIntegrationTest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config.AuditorAwareConfig;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config.JpaAuditingConfig;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerSearchCriteria;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.specification.CustomerSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import({AuditorAwareConfig.class, JpaAuditingConfig.class})
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
                .phoneNumber("+352621234567").status(CustomerStatus.ACTIVE)
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
                .phoneNumber("+352621234567").status(CustomerStatus.ACTIVE)
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
                .phoneNumber("+352621234567").status(CustomerStatus.ACTIVE)
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
                .status(CustomerStatus.ACTIVE)
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
                        .status(CustomerStatus.ACTIVE)
                        .build());

        CustomerEntity johnny = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000002")
                        .firstName("Johnny")
                        .lastName("Doe")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Johnny@test.com")
                        .phoneNumber("+352621234567")
                        .status(CustomerStatus.ACTIVE)
                        .build());

        CustomerEntity alice = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000003")
                        .firstName("Alice")
                        .lastName("Brown")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Alice@test.com")
                        .phoneNumber("+352621234567")
                        .status(CustomerStatus.ACTIVE)
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
                        .status(CustomerStatus.ACTIVE)
                        .build());

        CustomerEntity johnny = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000002")
                        .firstName("Johnny")
                        .lastName("Doe")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Johnny@test.com")
                        .phoneNumber("+352621234567")
                        .status(CustomerStatus.ACTIVE)
                        .build());

        CustomerEntity alice = customerRepository.save(
                CustomerEntity.builder()
                        .customerNumber("C000000003")
                        .firstName("Alice")
                        .lastName("Brown")
                        .dateOfBirth(LocalDate.of(1995, 1, 1))
                        .email("Alice@test.com")
                        .phoneNumber("+352621234567")
                        .status(CustomerStatus.ACTIVE)
                        .build());

        Page<CustomerEntity> result =
                customerRepository.findByFirstNameContainingIgnoreCase(
                        "xyz",
                        pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldFindCustomerByFirstNameSpecification() {

        // Arrange
        CustomerEntity customer1 = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .status(CustomerStatus.ACTIVE)
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .customerNumber("C000000002")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .email("john@test.com")
                .phoneNumber("+352661111111")
                .status(CustomerStatus.ACTIVE)
                .build();

        customerRepository.save(customer1);
        customerRepository.save(customer2);

        // Act
        List<CustomerEntity> customers = customerRepository.findAll(CustomerSpecification.hasFirstName("Nizar"));

        // Assert
        assertEquals(1, customers.size());
        assertEquals("Nizar", customers.getFirst().getFirstName());
    }

    @Test
    void shouldReturnAllCustomersWhenFirstNameIsNull() {
        // Arrange
        CustomerEntity customer1 = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .status(CustomerStatus.ACTIVE)
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .customerNumber("C000000002")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .email("john@test.com")
                .phoneNumber("+352661111111")
                .status(CustomerStatus.ACTIVE)
                .build();

        customerRepository.save(customer1);
        customerRepository.save(customer2);

        List<CustomerEntity> customers = customerRepository.findAll(CustomerSpecification.hasFirstName(null));
        assertEquals(2, customers.size());
    }

    @Test
    void shouldFindCustomerByFirstNameAndLastNameSpecification() {
        // Arrange
        CustomerEntity customer1 = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .status(CustomerStatus.ACTIVE)
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .customerNumber("C000000002")
                .firstName("Nizar")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .email("john@test.com")
                .phoneNumber("+352661111111")
                .status(CustomerStatus.ACTIVE)
                .build();

        CustomerEntity customer3 = CustomerEntity.builder()
                .customerNumber("C000000003")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .email("johndoe@test.com")
                .phoneNumber("+352661111111")
                .status(CustomerStatus.ACTIVE)
                .build();

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);

        Specification<CustomerEntity> specification = CustomerSpecification.hasFirstName("Nizar").and(CustomerSpecification.hasLastName("Najid"));

        List<CustomerEntity> customers = customerRepository.findAll(specification);

        assertEquals(1, customers.size());
        assertEquals("Nizar", customers.getFirst().getFirstName());
        assertEquals("Najid", customers.getFirst().getLastName());
    }

    @Test
    void shouldFindCustomerUsingSearchCriteria() {
        // Arrange
        CustomerEntity customer1 = CustomerEntity.builder()
                .customerNumber("C000000001")
                .firstName("Nizar")
                .lastName("Najid")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .email("nizar@test.com")
                .phoneNumber("+352621234567")
                .status(CustomerStatus.ACTIVE)
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .customerNumber("C000000002")
                .firstName("Nizar")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .email("john@test.com")
                .phoneNumber("+352661111111")
                .status(CustomerStatus.ACTIVE)
                .build();

        CustomerEntity customer3 = CustomerEntity.builder()
                .customerNumber("C000000003")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 10))
                .email("johndoe@test.com")
                .phoneNumber("+352661111111")
                .status(CustomerStatus.ACTIVE)
                .build();

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);

        CustomerSearchCriteria criteria = CustomerSearchCriteria.builder()
                .firstName("Nizar")
                .lastName("Najid")
                .build();

        List<CustomerEntity> customers = customerRepository.findAll(CustomerSpecification.build(criteria));

        assertEquals(1, customers.size());
        assertEquals("Nizar", customers.getFirst().getFirstName());
        assertEquals("Najid", customers.getFirst().getLastName());
    }
}