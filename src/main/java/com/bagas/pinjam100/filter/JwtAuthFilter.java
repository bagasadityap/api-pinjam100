package com.bagas.pinjam100.filter;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.exception.UnauthorizedHandler;
import com.bagas.pinjam100.security.AppUser;
import com.bagas.pinjam100.service.auth.AppUserDetailsService;
import com.bagas.pinjam100.service.auth.CustomerAuthenticationService;
import com.bagas.pinjam100.service.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";
    private static final String PESAN_TOKEN_TIDAK_VALID = "Token tidak valid";

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;
    private final CustomerAuthenticationService customerAuthenticationService;
    private final UnauthorizedHandler unauthorizedHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parse(
                    header.substring(PREFIX.length())
            );

            String type = claims.get("type", String.class);

            if ("CUSTOMER".equals(type)) {
                Customer customer =
                        customerAuthenticationService.loadByPhoneNumber(
                                claims.getSubject()
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                customer,
                                null,
                                List.of()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            } else if ("BACK_OFFICE".equals(type)) {
                AppUser user =
                        userDetailsService.loadUserByUsername(
                                claims.getSubject()
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            } else {
                throw new JwtException(PESAN_TOKEN_TIDAK_VALID);
            }

        } catch (
                JwtException |
                UsernameNotFoundException |
                IllegalArgumentException ex
        ) {
            SecurityContextHolder.clearContext();
            unauthorizedHandler.response(
                    response,
                    PESAN_TOKEN_TIDAK_VALID
            );
            return;
        }

        chain.doFilter(request, response);
    }
}