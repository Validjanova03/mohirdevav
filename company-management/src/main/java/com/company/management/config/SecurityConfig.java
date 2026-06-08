package com.company.management.config;

import com.company.management.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Public
                .requestMatchers("/api/auth/**").permitAll()

                // ── Employee Module ──────────────────────────────────────────
                .requestMatchers(HttpMethod.POST,   "/api/employees/**")
                    .hasAnyRole("DIRECTOR", "HR_MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/employees/**")
                    .hasAnyRole("DIRECTOR", "HR_MANAGER")
                .requestMatchers(HttpMethod.PATCH,  "/api/employees/**")
                    .hasAnyRole("DIRECTOR", "HR_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/employees/**")
                    .hasAnyRole("DIRECTOR", "HR_MANAGER")
                .requestMatchers(HttpMethod.GET,    "/api/employees/**")
                    .hasAnyRole("DIRECTOR", "HR_MANAGER", "EMPLOYEE")

                // ── Customer Module ──────────────────────────────────────────
                .requestMatchers(HttpMethod.POST,   "/api/customers/**")
                    .hasRole("CUSTOMER_MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/customers/**")
                    .hasRole("CUSTOMER_MANAGER")
                .requestMatchers(HttpMethod.PATCH,  "/api/customers/**")
                    .hasRole("CUSTOMER_MANAGER")
                .requestMatchers(HttpMethod.GET,    "/api/customers/my")
                    .hasRole("CUSTOMER_MANAGER")
                .requestMatchers(HttpMethod.GET,    "/api/customers/all")
                    .hasRole("DIRECTOR")
                .requestMatchers(HttpMethod.GET,    "/api/customers/**")
                    .hasAnyRole("DIRECTOR", "CUSTOMER_MANAGER")

                // ── Sales Module ─────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST,   "/api/advertisements/**")
                    .hasRole("SALES_MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/advertisements/**")
                    .hasRole("SALES_MANAGER")
                .requestMatchers(HttpMethod.PATCH,  "/api/advertisements/**")
                    .hasRole("SALES_MANAGER")
                .requestMatchers(HttpMethod.GET,    "/api/advertisements/**")
                    .hasAnyRole("DIRECTOR", "SALES_MANAGER")

                // ── Statistics ───────────────────────────────────────────────
                .requestMatchers("/api/stats/**")
                    .hasRole("DIRECTOR")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
