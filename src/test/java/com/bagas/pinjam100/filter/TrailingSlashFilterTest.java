package com.bagas.pinjam100.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrailingSlashFilterTest")
class TrailingSlashFilterTest {

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TrailingSlashFilter trailingSlashFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternalTest {

        @Test
        @DisplayName("should redirect with permanent move when URI ends with a slash")
        void shouldRedirectWhenUriEndsWithSlash() throws ServletException, IOException {
            request.setRequestURI("/api/users/");

            trailingSlashFilter.doFilterInternal(request, response, filterChain);

            assertEquals(HttpStatus.MOVED_PERMANENTLY.value(), response.getStatus());
            assertEquals("/api/users", response.getHeader(HttpHeaders.LOCATION));
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("should redirect and preserve query string when URI ends with a slash")
        void shouldRedirectAndPreserveQueryStringWhenUriEndsWithSlash() throws ServletException, IOException {
            request.setRequestURI("/api/users/");
            request.setQueryString("page=1&size=10");

            trailingSlashFilter.doFilterInternal(request, response, filterChain);

            assertEquals(HttpStatus.MOVED_PERMANENTLY.value(), response.getStatus());
            assertEquals("/api/users?page=1&size=10", response.getHeader(HttpHeaders.LOCATION));
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("should continue filter chain when URI does not end with a slash")
        void shouldContinueWhenUriDoesNotEndWithSlash() throws ServletException, IOException {
            request.setRequestURI("/api/users");

            trailingSlashFilter.doFilterInternal(request, response, filterChain);

            assertEquals(HttpStatus.OK.value(), response.getStatus());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("should continue filter chain when URI is just root context ('/')")
        void shouldContinueWhenUriIsRootContext() throws ServletException, IOException {
            request.setRequestURI("/");

            trailingSlashFilter.doFilterInternal(request, response, filterChain);

            assertEquals(HttpStatus.OK.value(), response.getStatus());
            verify(filterChain).doFilter(request, response);
        }
    }
}
