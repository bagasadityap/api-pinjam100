package com.bagas.pinjam100.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GlobalExceptionHandlerTest")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class TestController {
        @GetMapping("/illegal-arg")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid argument message");
        }

        @GetMapping("/not-found")
        public void throwNotFound() {
            throw new EntityNotFoundException("Data tidak ditemukan");
        }

        @GetMapping("/business-rule")
        public void throwBusinessRule() {
            throw new BusinessRuleException("Aturan bisnis dilanggar");
        }

        @GetMapping("/unauthorized")
        public void throwUnauthorized() {
            throw new AuthenticationException("Autentikasi gagal");
        }

        @GetMapping("/forbidden")
        public void throwForbidden() {
            throw new ForbiddenException("Akses ditolak");
        }

        @GetMapping("/conflict")
        public void throwConflict() {
            throw new ConflictException("Data konflik");
        }

        @GetMapping("/invalid-fcm")
        public void throwInvalidFcm() {
            throw new InvalidFcmTokenException("Token FCM tidak valid");
        }

        @GetMapping("/global-ex")
        public void throwGlobal() {
            throw new RuntimeException("Unexpected error");
        }
    }

    @Test
    @DisplayName("should handle IllegalArgumentException and return 400")
    void shouldHandleIllegalArgument() throws Exception {
        mockMvc.perform(get("/illegal-arg"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid argument message"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("should handle EntityNotFoundException and return 404")
    void shouldHandleEntityNotFound() throws Exception {
        mockMvc.perform(get("/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data tidak ditemukan"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("should handle BusinessRuleException and return 400")
    void shouldHandleBusinessRule() throws Exception {
        mockMvc.perform(get("/business-rule"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Aturan bisnis dilanggar"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("should handle AuthenticationException and return 401")
    void shouldHandleAuthentication() throws Exception {
        mockMvc.perform(get("/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Autentikasi gagal"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("should handle ForbiddenException and return 403")
    void shouldHandleForbidden() throws Exception {
        mockMvc.perform(get("/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Akses ditolak"))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("should handle ConflictException and return 409")
    void shouldHandleConflict() throws Exception {
        mockMvc.perform(get("/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Data konflik"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("should handle InvalidFcmTokenException and return 404")
    void shouldHandleInvalidFcm() throws Exception {
        mockMvc.perform(get("/invalid-fcm"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Token FCM tidak valid"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("should handle generic Exception and return 500")
    void shouldHandleGlobalException() throws Exception {
        mockMvc.perform(get("/global-ex"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Terjadi kesalahan pada server"))
                .andExpect(jsonPath("$.status").value(500));
    }
}
