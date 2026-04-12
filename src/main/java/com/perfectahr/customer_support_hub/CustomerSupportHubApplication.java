package com.perfectahr.customer_support_hub;

import com.perfectahr.customer_support_hub.auth.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class CustomerSupportHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerSupportHubApplication.class, args);
	}

}
