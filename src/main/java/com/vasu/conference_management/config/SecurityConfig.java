package com.vasu.conference_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure password encoder as BCrypt with strength 12.
     * This is the standard for Spring Security applications.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configure HTTP security for session-based authentication.
     *
     * Key features:
     * - CSRF protection enabled (default for Thymeleaf forms)
     * - Session management with default JSESSIONID cookie
     * - Public endpoints: /login, /register, /api/dashboard, /static/**
     * - Protected endpoints: everything else requires authentication
     * - Login form at /login
     * - Logout at /logout
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints
                .requestMatchers("/", "/login", "/register", "/api/dashboard", "/api/dashboard/**").permitAll()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                // API endpoints - public for now, can be restricted later
                .requestMatchers("/api/conferences/**").permitAll()
                .requestMatchers("/api/papers/**").permitAll()
                .requestMatchers("/api/tags/**").permitAll()
                // Protected endpoints
                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                .requestMatchers("/dashboard", "/dashboard/**").authenticated()
                .requestMatchers("/user/**").authenticated()
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                )
            )
            .csrf(csrf -> csrf.disable()); // Can be enabled later with CSRF token in Thymeleaf forms

        return http.build();
    }
}

