package com.example.serbisyofullstack.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT security: CSRF disabled, no sessions, endpoint-level rules plus
 * method-level ownership checks in services.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationHandlers handlers;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(handlers.authenticationEntryPoint())
                .accessDeniedHandler(handlers.accessDeniedHandler()))
                // Baseline security headers for API responses. HSTS is injected
                // by the reverse proxy / load balancer when TLS terminates there.
                .headers(headers -> headers
                .contentTypeOptions(withDefaults -> {})
                .frameOptions(frame -> frame.deny())
                .referrerPolicy(referrer -> referrer.policy(
                        org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**", "/api/v1/services/**",
                        "/api/v1/providers/search", "/api/v1/providers/*",
                        "/api/v1/reviews/provider/*").permitAll()
                .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "MODERATOR")
                // Payment gateway callbacks are unauthenticated (verified by the
                // gateway reference + rate limited per IP in the controller).
                .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook").permitAll()
                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
