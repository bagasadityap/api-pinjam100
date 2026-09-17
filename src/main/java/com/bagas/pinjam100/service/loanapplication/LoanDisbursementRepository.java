package com.bagas.pinjam100.service.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanDisbursementRepository extends JpaRepository<LoanDisbursement, UUID> {
    List<LoanDisbursement> findByLoanApplication_Customer_Id(UUID customerId);
}
