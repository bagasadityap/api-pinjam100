package com.bagas.pinjam100.repository.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationDisbursementRepository extends JpaRepository<LoanDisbursement, UUID> {
}
