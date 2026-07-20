package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.repository;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByCustomerNumber(String customerNumber);

    Optional<CustomerEntity> findByEmail(String email);

    boolean existsByCustomerNumber(String customerNumber);

    boolean existsByEmail(String email);

    @Query(value = "SELECT nextval('customer_number_seq')", nativeQuery = true)
    Long getNextCustomerNumber();

    Page<CustomerEntity> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable
    );
}
