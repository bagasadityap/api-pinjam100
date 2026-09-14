package com.bagas.pinjam100.repository.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.LoanApplicationApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationApprovalRepository extends JpaRepository<LoanApplicationApproval, UUID> {
}
