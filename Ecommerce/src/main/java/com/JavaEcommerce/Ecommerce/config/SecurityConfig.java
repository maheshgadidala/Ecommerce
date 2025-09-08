package com.JavaEcommerce.Ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/v1/api/login").permitAll() // Allow login endpoint
                        .requestMatchers("/api/echo").permitAll() // Allow echo endpoint
                        .requestMatchers("/api/public/**").permitAll() // Allow public endpoints
                        .anyRequest().authenticated() // All other requests need authentication
                )
                .httpBasic(withDefaults()); // Enable HTTP Basic authentication

        return http.build();
    }
}
