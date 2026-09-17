package com.bagas.pinjam100.entity.loanapplication;

import com.bagas.pinjam100.entity.customer.Rekening;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_disbursement", schema = "loan")
public class LoanDisbursement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "disbursement_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal disbursementAmount;

    @Column(name = "transaction_reference", unique = true, nullable = false)
    private String transactionReference;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplication loanApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rekening_id", nullable = false)
    private Rekening rekening;
}
