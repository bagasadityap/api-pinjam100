package com.bagas.pinjam100.service.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenBlacklistServiceTest")
class TokenBlacklistServiceTest {

    private static final String SAMPLE_TOKEN = "eyJhbGciOiJIUzI1NiJ9.sample.token";

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Nested
    @DisplayName("isRevoked")
    class IsRevokedTest {

        @Test
        @DisplayName("should return false when token is not in blacklist")
        void shouldReturnFalseWhenTokenNotInBlacklist() {
            boolean result = tokenBlacklistService.isRevoked(SAMPLE_TOKEN);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when active token is in blacklist")
        void shouldReturnTrueWhenTokenIsInBlacklist() {
            Instant futureExpiry = Instant.now().plus(1, ChronoUnit.HOURS);
            tokenBlacklistService.revoke(SAMPLE_TOKEN, futureExpiry);

            boolean result = tokenBlacklistService.isRevoked(SAMPLE_TOKEN);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false and remove token from blacklist when token has expired")
        void shouldReturnFalseAndRemoveTokenWhenExpired() {
            Instant pastExpiry = Instant.now().minus(1, ChronoUnit.HOURS);
            tokenBlacklistService.revoke(SAMPLE_TOKEN, pastExpiry);

            boolean resultFirstCall = tokenBlacklistService.isRevoked(SAMPLE_TOKEN);
            assertFalse(resultFirstCall);

            boolean resultSecondCall = tokenBlacklistService.isRevoked(SAMPLE_TOKEN);
            assertFalse(resultSecondCall);
        }
    }

    @Nested
    @DisplayName("revoke")
    class RevokeTest {

        @Test
        @DisplayName("should add token to blacklist successfully")
        void shouldAddTokenToBlacklist() {
            Instant futureExpiry = Instant.now().plus(30, ChronoUnit.MINUTES);

            assertDoesNotThrow(() -> tokenBlacklistService.revoke(SAMPLE_TOKEN, futureExpiry));
            assertTrue(tokenBlacklistService.isRevoked(SAMPLE_TOKEN));
        }
    }
}
