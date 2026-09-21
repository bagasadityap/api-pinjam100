package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.entity.auth.CustomerRefreshToken;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.repository.auth.CustomerRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRefreshTokenServiceTest")
class CustomerRefreshTokenServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final long REFRESH_TOKEN_TTL_DAYS = 7;
    private static final String RAW_TOKEN = "sample-raw-refresh-token-123456789";

    @Mock
    private CustomerRefreshTokenRepository repository;

    @InjectMocks
    private CustomerRefreshTokenService customerRefreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                customerRefreshTokenService,
                "refreshTokenTtlDays",
                REFRESH_TOKEN_TTL_DAYS
        );
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("should create and save refresh token successfully")
        void shouldCreateRefreshTokenSuccessfully() {
            Customer customer = createCustomer();

            when(repository.save(any(CustomerRefreshToken.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            String rawToken = customerRefreshTokenService.create(customer);

            assertNotNull(rawToken);
            assertFalse(rawToken.isBlank());

            verify(repository).save(argThat(token ->
                    token.getCustomer().equals(customer) &&
                            token.getTokenHash() != null &&
                            token.getExpiresAt().isAfter(Instant.now())
            ));
        }
    }

    // =========================================================
    // VALIDATE
    // =========================================================

    @Nested
    @DisplayName("validate")
    class ValidateTest {

        @Test
        @DisplayName("should validate successfully when token is active and not expired")
        void shouldValidateSuccessfully() {
            CustomerRefreshToken refreshToken = createRefreshToken(false, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            CustomerRefreshToken result = customerRefreshTokenService.validate(RAW_TOKEN);

            assertNotNull(result);
            assertEquals(refreshToken, result);
            verify(repository).findByTokenHash(anyString());
        }

        @Test
        @DisplayName("should throw AuthenticationException when token is not found")
        void shouldThrowExceptionWhenTokenNotFound() {
            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerRefreshTokenService.validate(RAW_TOKEN)
            );

            assertEquals("Refresh token tidak valid", exception.getMessage());
        }

        @Test
        @DisplayName("should throw AuthenticationException when token is revoked")
        void shouldThrowExceptionWhenTokenIsRevoked() {
            CustomerRefreshToken refreshToken = createRefreshToken(true, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerRefreshTokenService.validate(RAW_TOKEN)
            );

            assertEquals("Refresh token sudah tidak berlaku", exception.getMessage());
        }

        @Test
        @DisplayName("should throw AuthenticationException when token is expired")
        void shouldThrowExceptionWhenTokenIsExpired() {
            CustomerRefreshToken refreshToken = createRefreshToken(false, true);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerRefreshTokenService.validate(RAW_TOKEN)
            );

            assertEquals("Refresh token sudah kedaluwarsa", exception.getMessage());
        }
    }

    // =========================================================
    // ROTATE
    // =========================================================

    @Nested
    @DisplayName("rotate")
    class RotateTest {

        @Test
        @DisplayName("should revoke old token and create new token on rotate")
        void shouldRotateTokenSuccessfully() {
            CustomerRefreshToken oldRefreshToken = createRefreshToken(false, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(oldRefreshToken));
            when(repository.save(any(CustomerRefreshToken.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            CustomerRefreshTokenService.RotatedRefreshToken result =
                    customerRefreshTokenService.rotate(RAW_TOKEN);

            assertNotNull(result);
            assertNotNull(result.getCustomer());
            assertNotNull(result.getRawToken());
            assertNotNull(oldRefreshToken.getRevokedAt());

            verify(repository, times(2)).save(any(CustomerRefreshToken.class));
        }

        @Test
        @DisplayName("should throw AuthenticationException on rotate when old token is revoked")
        void shouldThrowExceptionWhenOldTokenIsRevoked() {
            CustomerRefreshToken oldRefreshToken = createRefreshToken(true, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(oldRefreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerRefreshTokenService.rotate(RAW_TOKEN)
            );

            assertEquals("Refresh token sudah tidak berlaku", exception.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should throw AuthenticationException on rotate when old token is expired")
        void shouldThrowExceptionWhenOldTokenIsExpired() {
            CustomerRefreshToken oldRefreshToken = createRefreshToken(false, true);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(oldRefreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerRefreshTokenService.rotate(RAW_TOKEN)
            );

            assertEquals("Refresh token sudah kedaluwarsa", exception.getMessage());
            verify(repository, never()).save(any());
        }
    }

    // =========================================================
    // REVOKE
    // =========================================================

    @Nested
    @DisplayName("revoke")
    class RevokeTest {

        @Test
        @DisplayName("should revoke token successfully when it is active")
        void shouldRevokeTokenSuccessfully() {
            CustomerRefreshToken refreshToken = createRefreshToken(false, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            customerRefreshTokenService.revoke(RAW_TOKEN);

            assertNotNull(refreshToken.getRevokedAt());
            verify(repository).save(refreshToken);
        }

        @Test
        @DisplayName("should do nothing when token is already revoked")
        void shouldDoNothingWhenAlreadyRevoked() {
            CustomerRefreshToken refreshToken = createRefreshToken(true, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            customerRefreshTokenService.revoke(RAW_TOKEN);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should throw AuthenticationException on revoke when token not found")
        void shouldThrowExceptionWhenTokenNotFoundOnRevoke() {
            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> customerRefreshTokenService.revoke(RAW_TOKEN)
            );

            assertEquals("Refresh token tidak valid", exception.getMessage());
            verify(repository, never()).save(any());
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setPhoneNumber("08123456789");
        return customer;
    }

    private CustomerRefreshToken createRefreshToken(boolean revoked, boolean expired) {
        CustomerRefreshToken token = new CustomerRefreshToken();
        token.setCustomer(createCustomer());
        token.setTokenHash("mocked-hash");

        if (revoked) {
            token.setRevokedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        }

        if (expired) {
            token.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
        } else {
            token.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        }

        return token;
    }
}
