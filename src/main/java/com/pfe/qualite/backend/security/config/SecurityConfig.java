package com.pfe.qualite.backend.security.config;

import com.pfe.qualite.backend.security.jwt.JwtFilter;
import com.pfe.qualite.backend.security.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Config CORS personnalisée ici
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // Lecture (GET) autorisée à tous les rôles sur tous les modules
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "CHEF_PROJET", "PILOTE_QUALITE")
                        // CRUD Nomenclatures réservé à l'ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/nomenclatures", "/api/nomenclatures/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/nomenclatures/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/nomenclatures/**").hasRole("ADMIN")
                        // CRUD Utilisateurs réservé à l'ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/utilisateurs", "/api/admin/create-user").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/utilisateurs/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMIN")
                        // CRUD Fiche Qualité, Fiche Suivi : CHEF_PROJET (sur ses éléments) et ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/fiches", "/api/suivis").hasAnyRole("ADMIN", "CHEF_PROJET")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/fiches/**", "/api/suivis/**").hasAnyRole("ADMIN", "CHEF_PROJET")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/fiches/**", "/api/suivis/**").hasAnyRole("ADMIN", "CHEF_PROJET")
                        // CRUD Fiche Projet : CHEF_PROJET (sur ses projets) et ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/projets").hasAnyRole("ADMIN", "CHEF_PROJET")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/projets/**").hasAnyRole("ADMIN", "CHEF_PROJET")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/projets/**").hasAnyRole("ADMIN", "CHEF_PROJET")
                        // CRUD Notifications : ADMIN uniquement (création/suppression), lecture à tous
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/notifications", "/api/notifications/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/notifications/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/notifications/**").hasRole("ADMIN")
                        // Catch-all : ADMIN a tous les droits
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:4200")); // ✅ pour Angular local
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
    