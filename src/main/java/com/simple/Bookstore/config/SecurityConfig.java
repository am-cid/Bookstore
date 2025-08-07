package com.simple.Bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/api/v1/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/books/**",
                        ).permitAll()
                        // only admin can create, edit, and delete books.
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/books/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/books/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/books/*").hasRole("ADMIN")
                        // require authentication for everything else
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
