package com.bagas.pinjam100.repository.installment;

import com.bagas.pinjam100.entity.installment.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, UUID> {
    List<LoanInstallment> findByLoanApplication_Id(UUID loanApplicationId);
    List<LoanInstallment> findByLoanApplication_Customer_Id(UUID customerId);
}
