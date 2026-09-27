package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.AuthResponse;
import com.bagas.pinjam100.dto.auth.LoginRequest;
import com.bagas.pinjam100.dto.auth.LogoutRequest;
import com.bagas.pinjam100.dto.auth.RefreshTokenRequest;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.security.AppUser;
import com.bagas.pinjam100.service.auth.AppUserDetailsService;
import com.bagas.pinjam100.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthControllerTest")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private AppUserDetailsService appUserDetailsService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        authResponse = new AuthResponse();
    }

    @Nested
    @DisplayName("loginUser")
    class LoginUserTest {

        @Test
        @DisplayName("should login user successfully")
        void shouldLoginUserSuccessfully() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setIdentityNumber("12345678");
            request.setPassword("Password123!");

            AppUser appUser = mock(AppUser.class);
            when(appUserDetailsService.findUser("12345678")).thenReturn(Optional.of(appUser));
            when(authService.login(any(), any(LoginRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Login berhasil"));
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTest {

        @Test
        @DisplayName("should refresh token successfully")
        void shouldRefreshTokenSuccessfully() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken("mock-refresh-token");

            when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Token berhasil diperbarui"));
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTest {

        @Test
        @DisplayName("should logout successfully")
        void shouldLogoutSuccessfully() throws Exception {
            LogoutRequest request = new LogoutRequest();
            request.setRefreshToken("mock-refresh-token");

            mockMvc.perform(post("/auth/logout")
                            .header("Authorization", "Bearer mock-jwt-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Logout berhasil"));

            verify(authService).logout(eq("mock-jwt-token"), eq("mock-refresh-token"));
        }
    }

    @Nested
    @DisplayName("getCurrentUser")
    class GetCurrentUserTest {

        @Test
        @DisplayName("should return current user successfully")
        void shouldGetCurrentUserSuccessfully() throws Exception {
            User user = new User();
            user.setName("Bagas Aditya");

            when(authService.getCurrentUser()).thenReturn(user);

            mockMvc.perform(get("/auth/get-current-user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Berhasil mendapatkan data user"));
        }
    }
}
