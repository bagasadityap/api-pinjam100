package com.bagas.pinjam100.entity.auth;

import com.bagas.pinjam100.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "customer_refresh_tokens",
        indexes = {
                @Index(
                        name = "idx_customer_refresh_token_customer_id",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_customer_refresh_token_expires_at",
                        columnList = "expires_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CustomerRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(
            name = "revoked_at"
    )
    private Instant revokedAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}