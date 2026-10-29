package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.event;

public interface CustomerEventPublisher {

    void publishCustomerCreated(CustomerEvent event);

    void publishCustomerStatusChanged(CustomerEvent event);
}
