package com.pfe.qualite.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // désactiver CSRF pour les appels Postman
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // autoriser toutes les requêtes (temporairement)
                )
                .httpBasic(Customizer.withDefaults()); // activer auth basic (si besoin)
        return http.build();
    }
}
