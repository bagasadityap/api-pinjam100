package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.*;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.entity.otp.ResendOtpRequest;
import com.bagas.pinjam100.entity.otp.VerifyOtpRequest;
import com.bagas.pinjam100.service.auth.CustomerAuthService;
import com.bagas.pinjam100.service.auth.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerAuthControllerTest")
class CustomerAuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerAuthService customerAuthService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private CustomerAuthController customerAuthController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerAuthController).build();
    }

    @Nested
    @DisplayName("loginCustomer")
    class LoginCustomerTest {

        @Test
        @DisplayName("should login customer successfully")
        void shouldLoginCustomerSuccessfully() throws Exception {
            CustomerLoginRequest request = new CustomerLoginRequest();
            request.setPhoneNumber("08123456789");
            request.setPassword("Password123!");

            ResponseEntity<BaseResponse<CustomerAuthResponse>> mockResponse =
                    ResponseEntity.ok(BaseResponse.success("Login berhasil", new CustomerAuthResponse()));

            when(customerAuthService.login(any(CustomerLoginRequest.class), eq("Login berhasil")))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/auth/customer/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Login berhasil"));
        }
    }

    @Nested
    @DisplayName("registerCustomer")
    class RegisterCustomerTest {

        @Test
        @DisplayName("should register customer successfully")
        void shouldRegisterCustomerSuccessfully() throws Exception {
            CustomerRequest request = new CustomerRequest();
            request.setPhoneNumber("08123456789");

            ResponseEntity<BaseResponse<Void>> mockResponse =
                    ResponseEntity.ok(BaseResponse.success("Registrasi berhasil", null));

            when(customerAuthService.register(any(CustomerRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(post("/auth/customer/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("verifyOtp")
    class VerifyOtpTest {

        @Test
        @DisplayName("should verify otp successfully")
        void shouldVerifyOtpSuccessfully() throws Exception {
            VerifyOtpRequest request = new VerifyOtpRequest("08123456789", "123456");

            ResponseEntity<BaseResponse<CustomerAuthResponse>> mockResponse =
                    ResponseEntity.ok(BaseResponse.success("OTP verified", new CustomerAuthResponse()));

            when(customerAuthService.verifyOtp(any(VerifyOtpRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(post("/auth/customer/verify-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("resendOtp")
    class ResendOtpTest {

        @Test
        @DisplayName("should resend otp successfully")
        void shouldResendOtpSuccessfully() throws Exception {
            ResendOtpRequest request = new ResendOtpRequest("08123456789");

            ResponseEntity<BaseResponse<Void>> mockResponse =
                    ResponseEntity.ok(BaseResponse.success("OTP resent", null));

            when(customerAuthService.resendOtp(any(ResendOtpRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(post("/auth/customer/resend-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTest {

        @Test
        @DisplayName("should refresh token successfully")
        void shouldRefreshTokenSuccessfully() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("mock-refresh-token");

            ResponseEntity<BaseResponse<CustomerAuthResponse>> mockResponse =
                    ResponseEntity.ok(BaseResponse.success("Token refreshed", new CustomerAuthResponse()));

            when(customerAuthService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(post("/auth/customer/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTest {

        @Test
        @DisplayName("should logout customer successfully")
        void shouldLogoutSuccessfully() throws Exception {
            LogoutRequest request = new LogoutRequest();
            request.setRefreshToken("mock-refresh-token");

            ResponseEntity<Void> mockResponse = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

            when(customerAuthService.logout(eq("mock-jwt-token"), eq("mock-refresh-token")))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/auth/customer/logout")
                            .header("Authorization", "Bearer mock-jwt-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(customerAuthService).logout("mock-jwt-token", "mock-refresh-token");
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTest {

        @Test
        @DisplayName("should change password successfully")
        void shouldChangePasswordSuccessfully() throws Exception {
            ChangePasswordRequest request = new ChangePasswordRequest("OldPass123!", "NewPass123!");

            ResponseEntity<BaseResponse<Void>> mockResponse =
                    ResponseEntity.ok(BaseResponse.success("Password changed", null));

            when(customerAuthService.changePassword(eq("mock-jwt-token"), any(ChangePasswordRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/auth/customer/change-password")
                            .header("Authorization", "Bearer mock-jwt-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("forgotPassword")
    class ForgotPasswordTest {

        @Test
        @DisplayName("should request forgot password successfully")
        void shouldForgotPasswordSuccessfully() throws Exception {
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("customer@example.com");

            mockMvc.perform(post("/auth/customer/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Password berhasil diubah"));

            verify(passwordResetService).requestReset("customer@example.com");
        }
    }

    @Nested
    @DisplayName("resetPassword")
    class ResetPasswordTest {

        @Test
        @DisplayName("should reset password successfully")
        void shouldResetPasswordSuccessfully() throws Exception {
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setToken("reset-token");
            request.setPassword("NewPass123!");

            mockMvc.perform(post("/auth/customer/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Password berhasil diubah"));

            verify(passwordResetService).resetPassword(any(ResetPasswordRequest.class));
        }
    }
}
