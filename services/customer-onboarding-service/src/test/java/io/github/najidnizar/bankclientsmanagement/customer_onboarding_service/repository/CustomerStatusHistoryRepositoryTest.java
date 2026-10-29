package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config.AbstractPostgresIntegrationTest;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config.AuditorAwareConfig;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config.JpaAuditingConfig;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatusHistoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Import({AuditorAwareConfig.class, JpaAuditingConfig.class})
@DataJpaTest
class CustomerStatusHistoryRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CustomerStatusHistoryRepository customerStatusHistoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldPersistCustomerStatusHistory() {
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

        customerRepository.saveAndFlush(customer);

        CustomerStatusHistoryEntity history = CustomerStatusHistoryEntity.builder()
                .id(UUID.randomUUID())
                .customer(customer)
                .oldStatus(CustomerStatus.ACTIVE)
                .newStatus(CustomerStatus.BLOCKED)
                .changedAt(LocalDateTime.now())
                .changedBy("SYSTEM")
                .reason("Test")
                .build();

        CustomerStatusHistoryEntity saved = customerStatusHistoryRepository.saveAndFlush(history);

        Optional<CustomerStatusHistoryEntity> result = customerStatusHistoryRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals(customer.getId(), result.get().getCustomer().getId());
        assertEquals(CustomerStatus.ACTIVE, result.get().getOldStatus());
        assertEquals(CustomerStatus.BLOCKED, result.get().getNewStatus());
        assertEquals("SYSTEM", result.get().getChangedBy());
        assertEquals("Test", result.get().getReason());





    }


}