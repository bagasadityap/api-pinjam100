package com.bagas.pinjam100.repository.auth;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.auth.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByCustomer(Customer customer);

}
