package com.bagas.pinjam100.service.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordResetTokenGeneratorTest")
class PasswordResetTokenGeneratorTest {

    // =========================================================
    // GENERATE
    // =========================================================

    @Nested
    @DisplayName("generate")
    class GenerateTest {

        @Test
        @DisplayName("should generate non-null and non-empty token")
        void shouldGenerateNonNullAndNonEmptyToken() {
            String token = PasswordResetTokenGenerator.generate();

            assertNotNull(token);
            assertFalse(token.isBlank());
        }

        @Test
        @DisplayName("should generate URL-safe Base64 token with length 43")
        void shouldGenerateValidUrlSafeBase64TokenLength() {
            String token = PasswordResetTokenGenerator.generate();

            // 32 bytes encoded to Base64 without padding will always have 43 characters
            assertEquals(43, token.length());

            // URL-safe Base64 character check (contains no '+', '/', or '=')
            assertFalse(token.contains("+"));
            assertFalse(token.contains("/"));
            assertFalse(token.contains("="));
        }

        @Test
        @DisplayName("should generate unique tokens on multiple invocations")
        void shouldGenerateUniqueTokens() {
            String token1 = PasswordResetTokenGenerator.generate();
            String token2 = PasswordResetTokenGenerator.generate();

            assertNotEquals(token1, token2);
        }
    }

    // =========================================================
    // UTILITY CLASS REFLECTION (100% COVERAGE)
    // =========================================================

    @Nested
    @DisplayName("constructor")
    class ConstructorTest {

        @Test
        @DisplayName("should have private constructor to prevent instantiation")
        void shouldHavePrivateConstructor() throws Exception {
            Constructor<PasswordResetTokenGenerator> constructor =
                    PasswordResetTokenGenerator.class.getDeclaredConstructor();

            assertTrue(Modifier.isPrivate(constructor.getModifiers()));

            constructor.setAccessible(true);

            PasswordResetTokenGenerator instance = constructor.newInstance();
            assertNotNull(instance);
        }
    }
}
