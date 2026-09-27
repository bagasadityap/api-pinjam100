package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.ResetPasswordRequest;
import com.bagas.pinjam100.entity.auth.PasswordResetToken;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.repository.auth.PasswordResetTokenRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.service.email.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetServiceTest")
class PasswordResetServiceTest {

    private static final String EMAIL = "customer@example.com";
    private static final String UNKNOWN_EMAIL = "unknown@example.com";
    private static final String TOKEN = "valid-reset-token-123";
    private static final String INVALID_TOKEN = "invalid-token-404";
    private static final String NEW_PASSWORD = "NewPassword123!";
    private static final String ENCODED_PASSWORD = "encoded_new_password";

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    // =========================================================
    // REQUEST RESET
    // =========================================================

    @Nested
    @DisplayName("requestReset")
    class RequestResetTest {

        @Test
        @DisplayName("should create reset token and send email when customer email exists")
        void shouldRequestResetSuccessfully() {
            Customer customer = createCustomer();

            when(customerRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(customer));
            when(tokenRepository.save(any(PasswordResetToken.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            passwordResetService.requestReset(EMAIL);

            verify(customerRepository).findByEmail(EMAIL);
            verify(tokenRepository).deleteByCustomer(customer);
            verify(tokenRepository).save(argThat(token ->
                    token.getCustomer().equals(customer) &&
                            token.getToken() != null &&
                            !token.isUsed() &&
                            token.getExpiresAt().isAfter(LocalDateTime.now())
            ));
            verify(emailService).sendPasswordResetEmail(eq(EMAIL), contains("https://pinjam100.bagasaditya.com/reset-password?token="));
        }

        @Test
        @DisplayName("should return early and do nothing when customer email is not found")
        void shouldDoNothingWhenCustomerNotFound() {
            when(customerRepository.findByEmail(UNKNOWN_EMAIL))
                    .thenReturn(Optional.empty());

            passwordResetService.requestReset(UNKNOWN_EMAIL);

            verify(customerRepository).findByEmail(UNKNOWN_EMAIL);
            verify(tokenRepository, never()).deleteByCustomer(any());
            verify(tokenRepository, never()).save(any());
            verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
        }
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @Nested
    @DisplayName("resetPassword")
    class ResetPasswordTest {

        @Test
        @DisplayName("should reset password and mark token as used successfully")
        void shouldResetPasswordSuccessfully() {
            ResetPasswordRequest request = createResetPasswordRequest(TOKEN, NEW_PASSWORD);
            PasswordResetToken resetToken = createPasswordResetToken(false, false);

            when(tokenRepository.findByToken(TOKEN))
                    .thenReturn(Optional.of(resetToken));
            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);

            passwordResetService.resetPassword(request);

            assertTrue(resetToken.isUsed());
            assertEquals(ENCODED_PASSWORD, resetToken.getCustomer().getPassword());

            verify(tokenRepository).findByToken(TOKEN);
            verify(passwordEncoder).encode(NEW_PASSWORD);
            verify(customerRepository).save(resetToken.getCustomer());
            verify(tokenRepository).save(resetToken);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when token is not found")
        void shouldThrowExceptionWhenTokenNotFound() {
            ResetPasswordRequest request = createResetPasswordRequest(INVALID_TOKEN, NEW_PASSWORD);

            when(tokenRepository.findByToken(INVALID_TOKEN))
                    .thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword(request)
            );

            assertEquals("Token reset password tidak valid", exception.getMessage());
            verify(passwordEncoder, never()).encode(any());
            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when token is already used")
        void shouldThrowExceptionWhenTokenIsUsed() {
            ResetPasswordRequest request = createResetPasswordRequest(TOKEN, NEW_PASSWORD);
            PasswordResetToken resetToken = createPasswordResetToken(true, false);

            when(tokenRepository.findByToken(TOKEN))
                    .thenReturn(Optional.of(resetToken));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword(request)
            );

            assertEquals("Token reset password sudah digunakan", exception.getMessage());
            verify(passwordEncoder, never()).encode(any());
            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when token is expired")
        void shouldThrowExceptionWhenTokenIsExpired() {
            ResetPasswordRequest request = createResetPasswordRequest(TOKEN, NEW_PASSWORD);
            PasswordResetToken resetToken = createPasswordResetToken(false, true);

            when(tokenRepository.findByToken(TOKEN))
                    .thenReturn(Optional.of(resetToken));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword(request)
            );

            assertEquals("Token reset password sudah kedaluwarsa", exception.getMessage());
            verify(passwordEncoder, never()).encode(any());
            verify(customerRepository, never()).save(any());
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setEmail(EMAIL);
        customer.setPassword("old_password");
        return customer;
    }

    private PasswordResetToken createPasswordResetToken(boolean used, boolean expired) {
        PasswordResetToken token = new PasswordResetToken();
        token.setCustomer(createCustomer());
        token.setToken(TOKEN);
        token.setUsed(used);

        if (expired) {
            token.setExpiresAt(LocalDateTime.now().minusMinutes(10));
        } else {
            token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        }

        return token;
    }

    private ResetPasswordRequest createResetPasswordRequest(String token, String password) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setPassword(password);
        return request;
    }
}
