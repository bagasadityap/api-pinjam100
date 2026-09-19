package com.bagas.pinjam100.repository.customer;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByIdAndDeletedDateIsNull(UUID id);

    List<Customer> findAllByDeletedDateIsNull();

    Optional<Customer> findByPhoneNumberAndDeletedDateIsNull(
            String phoneNumber
    );

    boolean existsByPhoneNumberAndDeletedDateIsNull(
            String phoneNumber
    );

    boolean existsByEmailAndDeletedDateIsNull(
            String email
    );

    boolean existByNationalIdAndDeletedDateIsNull(
            String nationalId
    );

    Optional<Customer> findByEmail(String email);

    List<Customer> findByVerificationStatus(
            VerificationStatus verificationStatus
    );

    List<Customer> findTop5ByDeletedDateIsNullOrderByCreatedDateDesc();

    @Query("""
        SELECT c
        FROM Customer c
        WHERE c.verificationStatus = :status
          AND c.deletedDate IS NULL
          AND NOT EXISTS (
              SELECT 1
              FROM CustomerLimit cl
              WHERE cl.customer.id = c.id
          )
    """)
    List<Customer> findVerifiedAndLimitIsNull(
            @Param("status") VerificationStatus status
    );

    // Dashboard

    long countByDeletedDateIsNull();

    long countByVerificationStatusAndDeletedDateIsNull(
            VerificationStatus verificationStatus
    );

    @Query("""
        SELECT COUNT(c)
        FROM Customer c
        WHERE c.verificationStatus = :status
          AND c.deletedDate IS NULL
          AND NOT EXISTS (
              SELECT 1
              FROM CustomerLimit cl
              WHERE cl.customer.id = c.id
          )
    """)
    long countVerifiedAndLimitIsNull(
            @Param("status") VerificationStatus status
    );

    @Query("""
        SELECT COUNT(c)
        FROM Customer c
        WHERE c.createdDate >= :startDate
          AND c.createdDate < :endDate
          AND c.deletedDate IS NULL
    """)
    long countCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}