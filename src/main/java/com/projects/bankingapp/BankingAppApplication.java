package com.projects.bankingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
			title = "Banking App",
			description = "Backend Rest APIs for the Banking App",
			version = "v1.9",
			contact = @Contact(
				name = "Kester Ilo",
				email = "kester.dev@gmail.com",
				url = "https://github.com/kesterilo/bankingapp"
			),
			license = @License(
				name = "Banking App",
				url = "https://github.com/kesterilo/bankingapp"	
			)
	),
	externalDocs = @ExternalDocumentation(
		description = "The Banking App Documentation",
		url = "https://github.com/kesterilo/bankingapp"
	)
)
public class BankingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingAppApplication.class, args);
	}

}
