package com.bagas.pinjam100.entity.loanapplication;

import com.bagas.pinjam100.entity.userrolepermission.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_application_review", schema = "loan")
public class LoanApplicationReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewResult result;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @CreationTimestamp
    @Column(name = "reviewed_date", nullable = false)
    private LocalDateTime reviewedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;
}
