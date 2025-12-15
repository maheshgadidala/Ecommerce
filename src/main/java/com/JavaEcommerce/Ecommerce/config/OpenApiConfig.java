package com.JavaEcommerce.Ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 / Swagger Configuration
 * Defines API documentation and general API info
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0.0")
                        .description("Complete E-Commerce API with authentication, products, cart, addresses, orders, and payments")
                        .contact(new Contact()
                                .name("E-Commerce Support")
                                .email("support@ecommerce.com")));
    }
}






