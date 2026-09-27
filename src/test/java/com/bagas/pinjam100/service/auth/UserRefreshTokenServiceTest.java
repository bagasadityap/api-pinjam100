package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.entity.auth.UserRefreshToken;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.repository.auth.UserRefreshTokenRepository;
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
@DisplayName("UserRefreshTokenServiceTest")
class UserRefreshTokenServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final long REFRESH_TOKEN_TTL_DAYS = 7;
    private static final String RAW_TOKEN = "sample-raw-user-refresh-token-123456789";

    @Mock
    private UserRefreshTokenRepository repository;

    @InjectMocks
    private UserRefreshTokenService userRefreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                userRefreshTokenService,
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
            User user = createUser();

            when(repository.save(any(UserRefreshToken.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            String rawToken = userRefreshTokenService.create(user);

            assertNotNull(rawToken);
            assertFalse(rawToken.isBlank());

            verify(repository).save(argThat(token ->
                    token.getUser().equals(user) &&
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
            UserRefreshToken refreshToken = createRefreshToken(false, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            UserRefreshToken result = userRefreshTokenService.validate(RAW_TOKEN);

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
                    () -> userRefreshTokenService.validate(RAW_TOKEN)
            );

            assertEquals("Refresh token tidak valid", exception.getMessage());
        }

        @Test
        @DisplayName("should throw AuthenticationException when token is revoked")
        void shouldThrowExceptionWhenTokenIsRevoked() {
            UserRefreshToken refreshToken = createRefreshToken(true, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> userRefreshTokenService.validate(RAW_TOKEN)
            );

            assertEquals("Refresh token sudah tidak berlaku", exception.getMessage());
        }

        @Test
        @DisplayName("should throw AuthenticationException when token is expired")
        void shouldThrowExceptionWhenTokenIsExpired() {
            UserRefreshToken refreshToken = createRefreshToken(false, true);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> userRefreshTokenService.validate(RAW_TOKEN)
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
            UserRefreshToken oldRefreshToken = createRefreshToken(false, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(oldRefreshToken));
            when(repository.save(any(UserRefreshToken.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserRefreshTokenService.RotatedRefreshToken result =
                    userRefreshTokenService.rotate(RAW_TOKEN);

            assertNotNull(result);
            assertNotNull(result.getUser());
            assertNotNull(result.getRawToken());
            assertNotNull(oldRefreshToken.getRevokedAt());

            verify(repository, times(2)).save(any(UserRefreshToken.class));
        }

        @Test
        @DisplayName("should throw AuthenticationException on rotate when old token is revoked")
        void shouldThrowExceptionWhenOldTokenIsRevoked() {
            UserRefreshToken oldRefreshToken = createRefreshToken(true, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(oldRefreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> userRefreshTokenService.rotate(RAW_TOKEN)
            );

            assertEquals("Refresh token sudah tidak berlaku", exception.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should throw AuthenticationException on rotate when old token is expired")
        void shouldThrowExceptionWhenOldTokenIsExpired() {
            UserRefreshToken oldRefreshToken = createRefreshToken(false, true);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(oldRefreshToken));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> userRefreshTokenService.rotate(RAW_TOKEN)
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
            UserRefreshToken refreshToken = createRefreshToken(false, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            userRefreshTokenService.revoke(RAW_TOKEN);

            assertNotNull(refreshToken.getRevokedAt());
            verify(repository).save(refreshToken);
        }

        @Test
        @DisplayName("should do nothing when token is already revoked")
        void shouldDoNothingWhenAlreadyRevoked() {
            UserRefreshToken refreshToken = createRefreshToken(true, false);

            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(refreshToken));

            userRefreshTokenService.revoke(RAW_TOKEN);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should throw AuthenticationException on revoke when token not found")
        void shouldThrowExceptionWhenTokenNotFoundOnRevoke() {
            when(repository.findByTokenHash(anyString()))
                    .thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> userRefreshTokenService.revoke(RAW_TOKEN)
            );

            assertEquals("Refresh token tidak valid", exception.getMessage());
            verify(repository, never()).save(any());
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setIdentityNumber("1234567890");
        return user;
    }

    private UserRefreshToken createRefreshToken(boolean revoked, boolean expired) {
        UserRefreshToken token = new UserRefreshToken();
        token.setUser(createUser());
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
