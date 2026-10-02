package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCustomerEventPublisher implements CustomerEventPublisher {

    private static final String CUSTOMER_EVENTS_TOPIC = "customer-events";

    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    @Override
    public void publishCustomerCreated(CustomerEvent event) {
        kafkaTemplate.send(
                CUSTOMER_EVENTS_TOPIC,
                event.customerId().toString(),
                event
        );
    }

    @Override
    public void publishCustomerStatusChanged(CustomerEvent event) {
        kafkaTemplate.send(
                CUSTOMER_EVENTS_TOPIC,
                event.customerId().toString(),
                event
        );
    }
}
