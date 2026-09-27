package com.bagas.pinjam100.repository.auth;

import com.bagas.pinjam100.entity.auth.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRefreshTokenRepository
        extends JpaRepository<UserRefreshToken, UUID> {

    Optional<UserRefreshToken> findByTokenHash(String tokenHash);
}
