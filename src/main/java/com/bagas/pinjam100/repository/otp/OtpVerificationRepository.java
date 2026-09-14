package com.bagas.pinjam100.repository.otp;

import com.bagas.pinjam100.entity.otp.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

    Optional<OtpVerification>
    findTopByPhoneNumberAndVerifiedFalseOrderByCreatedDateDesc(
            String phoneNumber
    );
}
