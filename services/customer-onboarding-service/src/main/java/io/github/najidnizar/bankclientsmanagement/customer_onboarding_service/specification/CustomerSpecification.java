package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.specification;

import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.dto.CustomerSearchCriteria;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerEntity;
import io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.entity.CustomerStatus;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecification {

    private CustomerSpecification() {
    }

    public static Specification<CustomerEntity> hasFirstName(String firstName) {

        return (root, query, criteriaBuilder) -> {

            if (firstName == null || firstName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("firstName"), firstName);
        };
    }

    public static Specification<CustomerEntity> hasLastName(String lastName) {

        return (root, query, criteriaBuilder) -> {

            if (lastName == null || lastName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("lastName"), lastName);
        };
    }

    public static Specification<CustomerEntity> hasEmail(String email) {

        return (root, query, criteriaBuilder) -> {

            if (email == null || email.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("email"), email);
        };
    }

    public static Specification<CustomerEntity> hasCustomerNumber(String customerNumber) {

        return (root, query, criteriaBuilder) -> {

            if (customerNumber == null || customerNumber.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("customerNumber"), customerNumber);
        };
    }

    public static Specification<CustomerEntity> hasStatus(CustomerStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<CustomerEntity> build(CustomerSearchCriteria criteria) {
        return Specification.where(hasFirstName(criteria.firstName()))
                .and(hasLastName(criteria.lastName()))
                .and(hasEmail(criteria.email()))
                .and(hasCustomerNumber(criteria.customerNumber()))
                .and(hasStatus(criteria.status()));
    }



}
