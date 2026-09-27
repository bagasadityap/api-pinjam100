package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.entity.auth.UserRefreshToken;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.repository.auth.UserRefreshTokenRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserRefreshTokenService {

    private final UserRefreshTokenRepository repository;

    @Value("${app.security.refresh-token-ttl-days}")
    private long refreshTokenTtlDays;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String create(User user) {

        String rawToken = generateToken();

        UserRefreshToken refreshToken = new UserRefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(
                hash(rawToken)
        );
        refreshToken.setExpiresAt(
                Instant.now().plus(
                        refreshTokenTtlDays,
                        ChronoUnit.DAYS
                )
        );
        repository.save(refreshToken);

        return rawToken;
    }

    @Transactional(readOnly = true)
    public UserRefreshToken validate(
            String rawToken
    ) {

        UserRefreshToken refreshToken =
                repository.findByTokenHash(
                        hash(rawToken)
                ).orElseThrow(() ->
                        new AuthenticationException(
                                "Refresh token tidak valid"
                        )
                );

        if (refreshToken.getRevokedAt() != null) {
            throw new AuthenticationException(
                    "Refresh token sudah tidak berlaku"
            );
        }

        if (refreshToken.getExpiresAt()
                .isBefore(Instant.now())) {

            throw new AuthenticationException(
                    "Refresh token sudah kedaluwarsa"
            );
        }

        return refreshToken;
    }

    @Transactional
    public RotatedRefreshToken rotate(
            String rawToken
    ) {

        UserRefreshToken oldRefreshToken =
                repository.findByTokenHash(
                        hash(rawToken)
                ).orElseThrow(() ->
                        new AuthenticationException(
                                "Refresh token tidak valid"
                        )
                );

        if (oldRefreshToken.getRevokedAt() != null) {
            throw new AuthenticationException(
                    "Refresh token sudah tidak berlaku"
            );
        }

        if (oldRefreshToken.getExpiresAt()
                .isBefore(Instant.now())) {

            throw new AuthenticationException(
                    "Refresh token sudah kedaluwarsa"
            );
        }

        User user =
                oldRefreshToken.getUser();

        oldRefreshToken.setRevokedAt(
                Instant.now()
        );

        repository.save(
                oldRefreshToken
        );

        String newRawToken =
                create(user);

        return new RotatedRefreshToken(
                user,
                newRawToken
        );
    }

    @Transactional
    public void revoke(
            String rawToken
    ) {

        UserRefreshToken refreshToken =
                repository.findByTokenHash(
                        hash(rawToken)
                ).orElseThrow(() ->
                        new AuthenticationException(
                                "Refresh token tidak valid"
                        )
                );

        if (refreshToken.getRevokedAt() != null) {
            return;
        }

        refreshToken.setRevokedAt(
                Instant.now()
        );

        repository.save(
                refreshToken
        );
    }

    private String generateToken() {

        byte[] bytes =
                new byte[64];

        secureRandom.nextBytes(
                bytes
        );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hash(
            String token
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(
                                0xff & b
                        );

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(
                        hex
                );
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm tidak tersedia",
                    e
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class RotatedRefreshToken {

        private final User user;
        private final String rawToken;
    }
}