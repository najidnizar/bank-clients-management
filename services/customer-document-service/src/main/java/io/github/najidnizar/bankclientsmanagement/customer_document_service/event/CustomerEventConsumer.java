package io.github.najidnizar.bankclientsmanagement.customer_document_service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerEventConsumer {

    @KafkaListener(topics = "customer-events", groupId = "document-service")
    public void consume(CustomerEvent event) {
        switch (event.eventType()) {
            case CUSTOMER_CREATED ->
                    log.info("Customer created event received for customer {}", event.customerId());

            case CUSTOMER_STATUS_CHANGED ->
                    log.info("Customer status changed to {} for customer {}", event.status(), event.customerId());
        }
    }
}