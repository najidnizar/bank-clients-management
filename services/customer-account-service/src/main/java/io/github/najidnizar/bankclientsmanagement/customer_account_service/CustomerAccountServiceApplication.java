package io.github.najidnizar.bankclientsmanagement.customer_account_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CustomerAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerAccountServiceApplication.class, args);
	}

}
