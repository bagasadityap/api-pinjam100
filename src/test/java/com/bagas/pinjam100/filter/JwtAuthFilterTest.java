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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilterTest")
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AppUserDetailsService userDetailsService;

    @Mock
    private CustomerAuthenticationService customerAuthenticationService;

    @Mock
    private UnauthorizedHandler unauthorizedHandler;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternalTest {

        @Test
        @DisplayName("should skip filter when authorization header is missing or invalid prefix")
        void shouldSkipWhenHeaderMissing() throws ServletException, IOException {
            request.addHeader(HttpHeaders.AUTHORIZATION, "Basic invalid-token");

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("should authenticate successfully when token type is CUSTOMER")
        void shouldAuthenticateCustomerSuccessfully() throws ServletException, IOException {
            String token = "valid-customer-token";
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            Claims claims = mock(Claims.class);
            when(claims.get("type", String.class)).thenReturn("CUSTOMER");
            when(claims.getSubject()).thenReturn("08123456789");

            Customer customer = new Customer();
            customer.setPhoneNumber("08123456789");

            when(jwtService.parse(token)).thenReturn(claims);
            when(customerAuthenticationService.loadByPhoneNumber("08123456789")).thenReturn(customer);

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals(customer, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should authenticate successfully when token type is BACK_OFFICE")
        void shouldAuthenticateBackOfficeSuccessfully() throws ServletException, IOException {
            String token = "valid-backoffice-token";
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            Claims claims = mock(Claims.class);
            when(claims.get("type", String.class)).thenReturn("BACK_OFFICE");
            when(claims.getSubject()).thenReturn("12345678");

            AppUser appUser = mock(AppUser.class);
            when(appUser.getAuthorities()).thenReturn(List.of());

            when(jwtService.parse(token)).thenReturn(claims);
            when(userDetailsService.loadUserByUsername("12345678")).thenReturn(appUser);

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals(appUser, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should throw JwtException when token type is unknown")
        void shouldHandleUnknownTokenType() throws ServletException, IOException {
            String token = "unknown-type-token";
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            Claims claims = mock(Claims.class);
            when(claims.get("type", String.class)).thenReturn("UNKNOWN");

            when(jwtService.parse(token)).thenReturn(claims);

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(unauthorizedHandler).response(eq(response), eq("Token tidak valid"));
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("should handle JwtException when token parsing fails")
        void shouldHandleJwtExceptionOnParse() throws ServletException, IOException {
            String token = "malformed-token";
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            when(jwtService.parse(token)).thenThrow(new JwtException("Malformed"));

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(unauthorizedHandler).response(eq(response), eq("Token tidak valid"));
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("should handle UsernameNotFoundException when user is not found")
        void shouldHandleUsernameNotFound() throws ServletException, IOException {
            String token = "valid-token-not-found-user";
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            Claims claims = mock(Claims.class);
            when(claims.get("type", String.class)).thenReturn("BACK_OFFICE");
            when(claims.getSubject()).thenReturn("unknown-user");

            when(jwtService.parse(token)).thenReturn(claims);
            when(userDetailsService.loadUserByUsername("unknown-user"))
                    .thenThrow(new UsernameNotFoundException("User tidak ditemukan"));

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(unauthorizedHandler).response(eq(response), eq("Token tidak valid"));
            verify(filterChain, never()).doFilter(request, response);
        }
    }
}
