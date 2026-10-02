package io.github.najidnizar.bankclientsmanagement.customer_kyc_service.event;

import io.github.najidnizar.bankclientsmanagement.customer_kyc_service.service.KycCaseService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventConsumer {

    private final KycCaseService kycCaseService;

    @PostConstruct
    public void init() {
        log.info("CustomerEventConsumer initialized");
    }

    @KafkaListener(topics = "customer-events", groupId = "kyc-service-v2")
    public void consume(CustomerEvent event) {
        log.info("Received CustomerEvent: {}", event);

        switch (event.eventType()) {
            case CustomerEventType.CUSTOMER_CREATED ->
                    kycCaseService.createFromCustomer(event.customerId());

            case CustomerEventType.CUSTOMER_STATUS_CHANGED ->
                    kycCaseService.handleCustomerStatusChanged(
                            event.customerId(),
                            event.status()
                    );

            default ->
                    log.warn("Unsupported customer event type: {}", event.eventType());
        }
    }
}