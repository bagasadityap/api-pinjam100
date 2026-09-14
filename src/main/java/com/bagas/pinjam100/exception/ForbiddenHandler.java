package com.bagas.pinjam100.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ForbiddenHandler {

    private final ObjectMapper objectMapper;

    public ForbiddenHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void response(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = Map.of(
                "status", HttpStatus.FORBIDDEN.value(),
                "error", HttpStatus.FORBIDDEN.getReasonPhrase(),
                "message", message
        );

        response.getWriter().write(
                objectMapper.writeValueAsString(body)
        );
    }
}