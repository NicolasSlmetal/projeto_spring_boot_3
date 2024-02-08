package com.estudo.slgames.swagger;


import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	
	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
				.title("Slgames-Api")
				.description("API de uma loja de jogos com Spring Boot 3, por enquanto, apenas com jogos")
				.version("1.0.0")
				.contact(new Contact()
						.name("Nicolas")
						.email("limabritopro@gmail.com"))
				.license(new License()
						.name("APACHE 2.0")
						.url("https://www.apache.org/licenses/LICENSE-2.0"))
				)
				.components(new Components()
						.addSecuritySchemes("bearerAuth", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT"))).security(Collections.singletonList(new SecurityRequirement().addList("bearerAuth")));
	}
}
