package com.bagas.pinjam100.service.otp;

import com.bagas.pinjam100.entity.otp.OtpVerification;
import com.bagas.pinjam100.repository.otp.OtpVerificationRepository;
import com.bagas.pinjam100.service.FlowKirimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpVerificationService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;

    private final OtpVerificationRepository otpVerificationRepository;
    private final FlowKirimService flowKirimService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String generate(String phoneNumber) {
        String otpCode = generateOtp();

        OtpVerification otpVerification = OtpVerification.builder()
                .phoneNumber(phoneNumber)
                .otpCode(otpCode)
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(OTP_EXPIRATION_MINUTES)
                )
                .verified(false)
                .attempts(0)
                .build();

        otpVerificationRepository.save(otpVerification);

        String response = flowKirimService.sendOtp(
                phoneNumber,
                otpCode
        );

        return otpCode;
    }

    @Transactional
    public void verify(String phoneNumber, String otpCode) {
        OtpVerification verification = otpVerificationRepository
                .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedDateDesc(phoneNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("OTP tidak ditemukan")
                );

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP sudah kedaluwarsa");
        }

        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Terlalu banyak percobaan");
        }

        if (!verification.getOtpCode().equals(otpCode)) {
            verification.setAttempts(verification.getAttempts() + 1);
            otpVerificationRepository.save(verification);

            throw new IllegalArgumentException("OTP tidak valid");
        }

        verification.setVerified(true);
        otpVerificationRepository.save(verification);
    }

    private String generateOtp() {
        return String.format(
                "%0" + OTP_LENGTH + "d",
                secureRandom.nextInt(1_000_000)
        );
    }
}
