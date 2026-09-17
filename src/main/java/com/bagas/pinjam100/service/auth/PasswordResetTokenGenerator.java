package com.bagas.pinjam100.service.auth;

import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordResetTokenGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordResetTokenGenerator() {
    }

    public static String generate() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
