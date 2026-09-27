package com.bagas.pinjam100.repository.auth;

import com.bagas.pinjam100.entity.auth.CustomerRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRefreshTokenRepository
        extends JpaRepository<CustomerRefreshToken, UUID> {

    Optional<CustomerRefreshToken> findByTokenHash(String tokenHash);
}