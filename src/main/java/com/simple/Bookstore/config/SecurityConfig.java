package com.simple.Bookstore.config;

import com.simple.Bookstore.Auth.AuthFailureHandler;
import com.simple.Bookstore.Auth.AuthSuccessHandler;
import com.simple.Bookstore.Auth.CustomLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    @Value("${security.remember-me.key:}")
    private String rememberMeKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (activeProfile.equals("prod") && rememberMeKey.isBlank()) {
            throw new IllegalStateException(
                    "You are in production and need to set remember-me key in " +
                            "application-prod.properties. It is not set by default"
            );
        }

        http
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/api/v1/**")
                        /* needed to create the csrf cookie every time and not
                        lazily persist which, while might be better for
                        performance, solves the race condition of using the
                        csrf token of the old session before it was invalidated
                        when logging out, causing the login form in the right
                        sidebar to bug out.
                        source: https://stackoverflow.com/questions/63626289/when-i-use-thaction-and-method-attribute-together-in-form-tag-in-thymeleaf-it/67192363#67192363
                         */
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )
                .sessionManagement((session) -> session
                        /* needed to always reinitialize the session. solves
                        the same problem as csrf.
                        source: same as csrf
                         */
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .authorizeHttpRequests(auth -> auth
                        // only admin can create, edit, and delete books.
                        .requestMatchers(HttpMethod.POST, "/api/v1/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/books/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/books/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/books/*").hasRole("ADMIN")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                        // only logged in users can GET POST PATCH DELETE reviews
                        .requestMatchers(HttpMethod.POST, "/api/v1/books/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/reviews/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/*").authenticated()
                        // for website viewing
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/books/**", "/api/v1/comments/**",
                                "/api/v1/reviews/**",
                                "/", "/search", "/register/**", "/profile/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/**", "/register/**",
                                "/profile/**"
                        ).permitAll()
                        // resources
                        .requestMatchers(HttpMethod.GET,
                                "/static/**", "/css/**", "/js/**", "/img/**",
                                "/fonts/**"
                        ).permitAll()
                        // require authentication for everything else
                        // easier for debugging anyway.
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/v1/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(new AuthSuccessHandler())
                        .failureHandler(new AuthFailureHandler())
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                        .permitAll()
                )
        ;
        return http.build();
    }

    /// for logging in post registration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
