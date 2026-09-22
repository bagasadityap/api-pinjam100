package com.bagas.pinjam100.config;

import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Value("${app.security.cors-allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                SUPER_ADMIN > ADMIN
                SUPER_ADMIN > USER
                SUPER_ADMIN > CUSTOMER
                SUPER_ADMIN > user:read
                SUPER_ADMIN > user:write
                SUPER_ADMIN > user:delete
                SUPER_ADMIN > role:read
                SUPER_ADMIN > role:write
                SUPER_ADMIN > role:delete
                SUPER_ADMIN > permission:read
                SUPER_ADMIN > permission:write
                SUPER_ADMIN > permission:delete
                SUPER_ADMIN > customer:read
                SUPER_ADMIN > customer:verify
                SUPER_ADMIN > dashboard:read
                SUPER_ADMIN > loan:read
                SUPER_ADMIN > loan:review
                SUPER_ADMIN > loan:approve
                SUPER_ADMIN > loan:disburse
                SUPER_ADMIN > loan:delete
                SUPER_ADMIN > branch:read
                SUPER_ADMIN > branch:write
                SUPER_ADMIN > branch:delete
                SUPER_ADMIN > document:verify
                SUPER_ADMIN > limit:write
                """);
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws AuthenticationException {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                        "script-src 'self' 'unsafe-inline'; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "img-src 'self' data: https:; " +
                                        "font-src 'self' data: https:; " +
                                        "connect-src 'self' http://api.pinjam100.bagasaditya.com https://api.pinjam100.bagasaditya.com; " +
                                        "frame-ancestors 'none'; " +
                                        "base-uri 'self'"
                        ))
                        .referrerPolicy(referer -> referer.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER
                        ))
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                        .frameOptions(frame -> frame.deny())
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/scalar",
                                "/docs",
                                "/v3/api-docs/**",
                                "/scalar/**",
                                "/auth/**",
                                "/uploads/files/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    Map<String, Object> body = Map.of(
                                            "message",
                                            "Silakan melakukan login",

                                            "error",
                                            "Unauthorized",

                                            "status",
                                            401,

                                            "timestamp",
                                            Instant.now().toString()
                                    );
                                    objectMapper.writeValue(response.getOutputStream(), body);
                                }
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setStatus(HttpStatus.FORBIDDEN.value());
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    Map<String, Object> body = Map.of(
                                            "message",
                                            "Anda tidak memiliki akses ke resource ini",

                                            "error",
                                            "Forbidden",

                                            "status",
                                            403,

                                            "timestamp",
                                            Instant.now().toString()
                                    );
                                    objectMapper.writeValue(response.getOutputStream(), body);
                                }
                        )
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        String baku = "bcrypt";
        Map<String, PasswordEncoder> encoders = Map.of(
                baku,
                new BCryptPasswordEncoder(12)
        );
        return new DelegatingPasswordEncoder(
                baku,
                encoders
        );
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}