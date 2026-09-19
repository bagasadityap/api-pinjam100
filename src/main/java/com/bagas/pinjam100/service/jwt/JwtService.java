package com.bagas.pinjam100.service.jwt;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.security.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final Duration ttl;

    public JwtService(
            @Value("${app.security.jwt-secret}") String secret,
            @Value("${app.security.jwt-ttl-minutes}") long ttlMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public String issue(AppUser user, Instant issuedAt) {
        return builder(user, issuedAt)
                .expiration(Date.from(issuedAt.plus(ttl)))
                .compact();
    }

    public String issueWithoutExpiry(AppUser user, Instant issuedAt) {
        return builder(user, issuedAt).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Instant getExpiration(String token) {
        return parse(token)
                .getExpiration()
                .toInstant();
    }

    private JwtBuilder builder(AppUser user, Instant issuedAt) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("type", "BACK_OFFICE")
                .claim("identityNumber", user.getIdentityNumber())
                .claim(
                        "role",
                        user.getRole() != null
                                ? user.getRole().getRoleName()
                                : null
                )
                .claim("idUser", user.getIdUser())
                .claim("branch", user.getBranch())
                .issuedAt(Date.from(issuedAt))
                .signWith(key);
    }

    public String getRole(String token) {
        return parse(token).get("role", String.class);
    }

    public String getBranch(String token) {
        return parse(token).get("branch", String.class);
    }

    public String issueCustomer(Customer customer, Instant issuedAt) {
        return customerBuilder(customer, issuedAt)
                .expiration(Date.from(issuedAt.plus(ttl)))
                .compact();
    }

    public String getUsername(String token) {
        return parse(token).getSubject();
    }

    public String issueCustomerWithoutExpiry(Customer customer, Instant issuedAt) {
        return customerBuilder(customer, issuedAt).compact();
    }

    private JwtBuilder customerBuilder(
            Customer customer,
            Instant issuedAt
    ) {
        return Jwts.builder()
                .subject(customer.getPhoneNumber())
                .claim("type", "CUSTOMER")
                .claim("idCustomer", customer.getId())
                .claim("customerNumber", customer.getCustomerNumber())
                .claim("nationalId", customer.getNationalId())
                .claim("fullName", customer.getFullName())
                .claim("email", customer.getEmail())
                .claim("phoneNumber", customer.getPhoneNumber())
                .claim("role", "CUSTOMER")
                .issuedAt(Date.from(issuedAt))
                .signWith(key);
    }
}