package io.github.najidnizar.bankclientsmanagement.customer_account_service.client;

import io.github.najidnizar.bankclientsmanagement.customer_account_service.exception.CustomerServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerServiceClient {

    @Value("${customer-service.url}")
    private String customerServiceUrl;

    private final RestClient restClient;

    public boolean customerExists(UUID customerId) {

        try {
            restClient.get()
                    .uri(customerServiceUrl + "/api/v1/customers/{id}", customerId)
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (HttpClientErrorException.NotFound exception) {
            return false;

        } catch (RestClientException exception) {
            throw new CustomerServiceUnavailableException(exception);
        }
    }
}
