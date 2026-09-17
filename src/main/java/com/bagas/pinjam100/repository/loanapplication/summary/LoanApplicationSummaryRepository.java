package com.bagas.pinjam100.repository.loanapplication.summary;

import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LoanApplicationSummaryRepository extends JpaRepository<LoanApplication, UUID> {

    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.deletedDate IS NULL
    """)
    long countAll();

    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.status = :status
        AND l.deletedDate IS NULL
    """)
    long countByStatus(@Param("status") LoanApplicationStatus status);

    @Query("""
        SELECT COALESCE(SUM(l.loanAmount), 0)
        FROM LoanApplication l
        WHERE l.deletedDate IS NULL
    """)
    BigDecimal sumLoanAmount();

    @Query("""
        SELECT COALESCE(SUM(l.loanAmount), 0)
        FROM LoanApplication l
        WHERE l.status = :status
        AND l.deletedDate IS NULL
    """)
    BigDecimal sumLoanAmountByStatus(
            @Param("status") LoanApplicationStatus status
    );

    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.createdDate >= :start
        AND l.createdDate < :end
        AND l.deletedDate IS NULL
    """)
    long countBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(l.loanAmount), 0)
        FROM LoanApplication l
        WHERE l.createdDate >= :start
        AND l.createdDate < :end
        AND l.deletedDate IS NULL
    """)
    BigDecimal sumLoanAmountBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT l.status, COUNT(l)
        FROM LoanApplication l
        WHERE l.deletedDate IS NULL
        GROUP BY l.status
    """)
    List<Object[]> countByStatus();

    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.status IN :statuses
        AND l.deletedDate IS NULL
    """)
    long countByStatuses(
            @Param("statuses") Collection<LoanApplicationStatus> statuses
    );

    @Query("""
        SELECT COUNT(DISTINCT la)
        FROM LoanApplication la
        JOIN LoanInstallment li
            ON li.loanApplication.id = la.id
        WHERE li.dueDate < :now
          AND la.deletedDate IS NULL
    """)
    long countOverdueLoans(
            @Param("now") LocalDate now
    );

    @Query("""
        SELECT COALESCE(SUM(li.installmentAmount), 0)
        FROM LoanApplication la
        JOIN LoanInstallment li
            ON li.loanApplication.id = la.id
        WHERE li.dueDate < :now
          AND la.deletedDate IS NULL
    """)
    BigDecimal sumOverdueLoanAmount(
            @Param("now") LocalDate now
    );

    @Query("""
    SELECT COUNT(la)
    FROM LoanApplication la
    WHERE la.deletedDate IS NULL
      AND la.createdDate >= :startDate
      AND la.createdDate < :endDate
""")
    long countCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}