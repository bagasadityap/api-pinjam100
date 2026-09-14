package com.bagas.pinjam100.service.jwt;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void revoke(String token, Instant expiresAt) {
        blacklist.put(token, expiresAt);
    }

    public boolean isRevoked(String token) {
        Instant expiresAt = blacklist.get(token);

        if (expiresAt == null) {
            return false;
        }

        if (expiresAt.isBefore(Instant.now())) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }
}