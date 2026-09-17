package com.bagas.pinjam100.repository.loanapplication;

import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    Optional<LoanApplication> findByIdAndDeletedDateIsNull(UUID id);
    List<LoanApplication> findAllByDeletedDateIsNull();
    List<LoanApplication> findByBranch_IdAndDeletedDateIsNull(UUID branchId);
    List<LoanApplication> findByCustomer_IdAndDeletedDateIsNull(UUID customerId);
    List<LoanApplication> findAllByStatusAndBranch_IdAndDeletedDateIsNull(
            LoanApplicationStatus status,
            UUID branchId
    );

    List<LoanApplication> findAllByStatusAndDeletedDateIsNull(LoanApplicationStatus loanApplicationStatus);

    List<LoanApplication> findTop5ByDeletedDateIsNullOrderByCreatedDateDesc();
    List<LoanApplication> findTop5ByBranch_IdAndDeletedDateIsNullOrderByCreatedDateDesc(UUID branchId);
}
