package com.bagas.pinjam100.entity.auth;

import com.bagas.pinjam100.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "password_reset_token",
        schema = "loan"
)
public class PasswordResetToken {

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
            nullable = false,
            unique = true,
            length = 128
    )
    private String token;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            nullable = false
    )
    private boolean used = false;

    @CreationTimestamp
    @Column(
            name = "created_date",
            nullable = false
    )
    private LocalDateTime createdDate;
}
