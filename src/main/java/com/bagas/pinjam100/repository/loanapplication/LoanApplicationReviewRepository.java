package com.bagas.pinjam100.repository.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.LoanApplicationReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationReviewRepository extends JpaRepository<LoanApplicationReview, UUID> {
    Optional<LoanApplicationReview> findByLoanApplication_Id(UUID id);
}
