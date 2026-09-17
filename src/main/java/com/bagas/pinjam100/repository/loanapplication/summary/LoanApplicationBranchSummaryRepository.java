package com.bagas.pinjam100.repository.loanapplication.summary;

import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoanApplicationBranchSummaryRepository extends JpaRepository<LoanApplication, UUID> {

    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.branch.id = :branchId
          AND l.deletedDate IS NULL
    """)
    long countAllByBranch(
            @Param("branchId") UUID branchId
    );

    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.branch.id = :branchId
          AND l.status = :status
          AND l.deletedDate IS NULL
    """)
    long countByStatusAndBranch(
            @Param("branchId") UUID branchId,
            @Param("status") LoanApplicationStatus status
    );

    @Query("""
        SELECT COALESCE(SUM(l.loanAmount), 0)
        FROM LoanApplication l
        WHERE l.branch.id = :branchId
          AND l.deletedDate IS NULL
    """)
    BigDecimal sumLoanAmountByBranch(
            @Param("branchId") UUID branchId
    );

    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.branch.id = :branchId
          AND l.createdDate >= :start
          AND l.createdDate < :end
          AND l.deletedDate IS NULL
    """)
    long countCreatedBetweenByBranch(
            @Param("branchId") UUID branchId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(l.loanAmount), 0)
        FROM LoanApplication l
        WHERE l.branch.id = :branchId
          AND l.createdDate >= :start
          AND l.createdDate < :end
          AND l.deletedDate IS NULL
    """)
    BigDecimal sumLoanAmountCreatedBetweenByBranch(
            @Param("branchId") UUID branchId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT l.status, COUNT(l)
        FROM LoanApplication l
        WHERE l.branch.id = :branchId
          AND l.deletedDate IS NULL
        GROUP BY l.status
    """)
    List<Object[]> countByStatusAndBranch(
            @Param("branchId") UUID branchId
    );

    @Query("""
        SELECT COUNT(DISTINCT la.id)
        FROM LoanApplication la
        JOIN LoanInstallment li
            ON li.loanApplication.id = la.id
        WHERE la.branch.id = :branchId
          AND la.deletedDate IS NULL
          AND li.dueDate < :now
    """)
    long countOverdueLoansByBranch(
            @Param("branchId") UUID branchId,
            @Param("now") LocalDate now
    );

    @Query("""
        SELECT COALESCE(SUM(li.installmentAmount), 0)
        FROM LoanApplication la
        JOIN LoanInstallment li
            ON li.loanApplication.id = la.id
        WHERE la.branch.id = :branchId
          AND la.deletedDate IS NULL
          AND li.dueDate < :now
    """)
    BigDecimal sumOverdueLoanAmountByBranch(
            @Param("branchId") UUID branchId,
            @Param("now") LocalDate now
    );
}