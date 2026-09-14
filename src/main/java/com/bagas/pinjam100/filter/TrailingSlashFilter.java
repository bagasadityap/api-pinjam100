package com.bagas.pinjam100.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class TrailingSlashFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // Skip root context and check if URI ends with a slash
        if (uri.length() > 1 && uri.endsWith("/")) {
            String cleanUrl = uri.substring(0, uri.length() - 1);

            // Append query parameters if present
            if (request.getQueryString() != null) {
                cleanUrl += "?" + request.getQueryString();
            }

            response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
            response.setHeader(HttpHeaders.LOCATION, cleanUrl);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

