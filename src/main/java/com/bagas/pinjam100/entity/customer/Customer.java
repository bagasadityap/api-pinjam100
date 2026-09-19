package com.bagas.pinjam100.entity.customer;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer", schema = "loan")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String customerNumber;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String email;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING_OTP_VERIFICATION;
    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted;
    @Column(name = "login_date")
    private LocalDateTime loginDate = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    @Column(name = "logout_date")
    private LocalDateTime logoutDate;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @OneToOne(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private CustomerDetail detail;

    @OneToOne(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private CustomerEmployment employment;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rekening> rekenings = new ArrayList<>();
}

