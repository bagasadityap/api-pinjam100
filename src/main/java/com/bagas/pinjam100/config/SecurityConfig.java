package com.bagas.pinjam100.config;

import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.exception.ForbiddenException;
import com.bagas.pinjam100.exception.ForbiddenHandler;
import com.bagas.pinjam100.filter.JwtAuthFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private JwtAuthFilter jwtAuthFilter;
    @Value("${app.security.cors-allowed-origins}")
    private List<String> allowedOrigins;
    private final ForbiddenHandler forbiddenHandler;

    @Bean
    SecurityFilterChain securityFilterChain (HttpSecurity http) throws AuthenticationException {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default src 'none'; frame-ancestors: 'none'; base uri: 'none'"))
                        .referrerPolicy(referer -> referer.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .frameOptions(frame -> frame.deny()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**", "/test/**", "/transaction-history/**", "/test/email").permitAll()
                        .requestMatchers("/document", "/document/**", "/installment").authenticated()
                        .requestMatchers("/user", "/user/**", "/role", "/role/**", "/permission", "/permission/**", "/branch", "/branch/**", "/wilayah", "/wilayah/**", "/customer", "/customer/**", "/loan-application", "/loan-application/**", "/customer-limit", "/customer-limit/**").authenticated()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                forbiddenHandler.response(
                                        response,
                                        "Anda tidak memiliki akses"
                                )
                        )
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        String baku = "bcrypt";
        Map<String, PasswordEncoder> encoders = Map.of(
                baku,
                new BCryptPasswordEncoder(12));
        return new DelegatingPasswordEncoder(baku, encoders);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration konfigurasi = new CorsConfiguration();
        konfigurasi.setAllowedOrigins(allowedOrigins);
        konfigurasi.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        konfigurasi.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        konfigurasi.setAllowCredentials(true);
        konfigurasi.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", konfigurasi);
        return source;
    }
}
