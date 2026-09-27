package com.bagas.pinjam100.service.otp;

import com.bagas.pinjam100.entity.otp.OtpVerification;
import com.bagas.pinjam100.repository.otp.OtpVerificationRepository;
import com.bagas.pinjam100.service.FlowKirimService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OtpVerificationServiceTest")
class OtpVerificationServiceTest {

    private static final String PHONE_NUMBER = "08123456789";
    private static final String VALID_OTP = "123456";
    private static final String INVALID_OTP = "654321";

    @Mock
    private OtpVerificationRepository otpVerificationRepository;

    @Mock
    private FlowKirimService flowKirimService;

    @InjectMocks
    private OtpVerificationService otpVerificationService;

    @Nested
    @DisplayName("generate")
    class GenerateTest {

        @Test
        @DisplayName("should generate, save, and send otp successfully")
        void shouldGenerateSaveAndSendOtpSuccessfully() {

            when(otpVerificationRepository.save(any(OtpVerification.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            String otpCode = otpVerificationService.generate(PHONE_NUMBER);

            // Verify returned OTP
            assertNotNull(otpCode);
            assertEquals(6, otpCode.length());
            assertTrue(otpCode.matches("\\d{6}"));

            // Verify OTP saved to database
            verify(otpVerificationRepository).save(argThat(otp ->
                    PHONE_NUMBER.equals(otp.getPhoneNumber()) &&
                            otpCode.equals(otp.getOtpCode()) &&
                            !otp.isVerified() &&
                            otp.getAttempts() == 0 &&
                            otp.getExpiresAt().isAfter(LocalDateTime.now())
            ));

            // Verify OTP sent through FlowKirim
            verify(flowKirimService).sendOtp(
                    PHONE_NUMBER,
                    otpCode
            );
        }
    }

    @Nested
    @DisplayName("verify")
    class VerifyTest {

        @Test
        @DisplayName("should verify otp successfully when otp is valid")
        void shouldVerifyOtpSuccessfully() {

            OtpVerification verification =
                    createOtpVerification(
                            VALID_OTP,
                            LocalDateTime.now().plusMinutes(3),
                            0
                    );

            when(
                    otpVerificationRepository
                            .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedDateDesc(
                                    PHONE_NUMBER
                            )
            ).thenReturn(Optional.of(verification));

            when(otpVerificationRepository.save(any(OtpVerification.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() ->
                    otpVerificationService.verify(
                            PHONE_NUMBER,
                            VALID_OTP
                    )
            );

            assertTrue(verification.isVerified());

            verify(otpVerificationRepository).save(verification);

            // Verify that FlowKirim is NOT called during verification
            verifyNoInteractions(flowKirimService);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when otp record is not found")
        void shouldThrowExceptionWhenOtpNotFound() {

            when(
                    otpVerificationRepository
                            .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedDateDesc(
                                    PHONE_NUMBER
                            )
            ).thenReturn(Optional.empty());

            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> otpVerificationService.verify(
                                    PHONE_NUMBER,
                                    VALID_OTP
                            )
                    );

            assertEquals(
                    "OTP tidak ditemukan",
                    exception.getMessage()
            );

            verify(
                    otpVerificationRepository,
                    never()
            ).save(any());

            verifyNoInteractions(flowKirimService);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when otp is expired")
        void shouldThrowExceptionWhenOtpIsExpired() {

            OtpVerification verification =
                    createOtpVerification(
                            VALID_OTP,
                            LocalDateTime.now().minusMinutes(1),
                            0
                    );

            when(
                    otpVerificationRepository
                            .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedDateDesc(
                                    PHONE_NUMBER
                            )
            ).thenReturn(Optional.of(verification));

            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> otpVerificationService.verify(
                                    PHONE_NUMBER,
                                    VALID_OTP
                            )
                    );

            assertEquals(
                    "OTP sudah kedaluwarsa",
                    exception.getMessage()
            );

            verify(
                    otpVerificationRepository,
                    never()
            ).save(any());

            verifyNoInteractions(flowKirimService);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when attempts reach max allowed")
        void shouldThrowExceptionWhenAttemptsReachMax() {

            OtpVerification verification =
                    createOtpVerification(
                            VALID_OTP,
                            LocalDateTime.now().plusMinutes(3),
                            5
                    );

            when(
                    otpVerificationRepository
                            .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedDateDesc(
                                    PHONE_NUMBER
                            )
            ).thenReturn(Optional.of(verification));

            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> otpVerificationService.verify(
                                    PHONE_NUMBER,
                                    VALID_OTP
                            )
                    );

            assertEquals(
                    "Terlalu banyak percobaan",
                    exception.getMessage()
            );

            verify(
                    otpVerificationRepository,
                    never()
            ).save(any());

            verifyNoInteractions(flowKirimService);
        }

        @Test
        @DisplayName("should increment attempts and throw IllegalArgumentException when otp is invalid")
        void shouldIncrementAttemptsWhenOtpIsInvalid() {

            OtpVerification verification =
                    createOtpVerification(
                            VALID_OTP,
                            LocalDateTime.now().plusMinutes(3),
                            1
                    );

            when(
                    otpVerificationRepository
                            .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedDateDesc(
                                    PHONE_NUMBER
                            )
            ).thenReturn(Optional.of(verification));

            when(otpVerificationRepository.save(any(OtpVerification.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> otpVerificationService.verify(
                                    PHONE_NUMBER,
                                    INVALID_OTP
                            )
                    );

            assertEquals(
                    "OTP tidak valid",
                    exception.getMessage()
            );

            assertEquals(
                    2,
                    verification.getAttempts()
            );

            assertFalse(
                    verification.isVerified()
            );

            verify(
                    otpVerificationRepository
            ).save(verification);

            verifyNoInteractions(flowKirimService);
        }
    }

    private OtpVerification createOtpVerification(
            String otpCode,
            LocalDateTime expiresAt,
            int attempts
    ) {
        return OtpVerification.builder()
                .phoneNumber(PHONE_NUMBER)
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .verified(false)
                .attempts(attempts)
                .build();
    }
}