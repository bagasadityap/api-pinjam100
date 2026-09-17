package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.ResetPasswordRequest;
import com.bagas.pinjam100.entity.auth.PasswordResetToken;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.repository.auth.PasswordResetTokenRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private static final int TOKEN_EXPIRY_MINUTES = 15;

    private final CustomerRepository customerRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void requestReset(String email) {
        Customer customer = customerRepository
                .findByEmail(email)
                .orElse(null);

        if (customer == null) {
            return;
        }

        tokenRepository.deleteByCustomer(customer);

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setCustomer(customer);
        resetToken.setToken(
                PasswordResetTokenGenerator.generate()
        );
        resetToken.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(TOKEN_EXPIRY_MINUTES)
        );
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        String resetLink =
                "https://pinjam100.bagasaditya.com/reset-password?token="
                        + resetToken.getToken();

        emailService.sendPasswordResetEmail(
                customer.getEmail(),
                resetLink
        );
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Token reset password tidak valid"
                        )
                );

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException(
                    "Token reset password sudah digunakan"
            );
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Token reset password sudah kedaluwarsa"
            );
        }

        Customer customer = resetToken.getCustomer();

        customer.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        customerRepository.save(customer);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}