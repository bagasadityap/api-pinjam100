package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.*;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.entity.otp.ResendOtpRequest;
import com.bagas.pinjam100.entity.otp.VerifyOtpRequest;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.exception.ConflictException;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.notification.CustomerDeviceRepository;
import com.bagas.pinjam100.service.jwt.JwtService;
import com.bagas.pinjam100.service.jwt.TokenBlacklistService;
import com.bagas.pinjam100.service.notification.CustomerDeviceService;
import com.bagas.pinjam100.service.otp.OtpVerificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final OtpVerificationService otpVerificationService;
    private final CustomerDeviceService customerDeviceService;
    private final CustomerDeviceRepository customerDeviceRepository;

    private static final String NOT_FOUND_MESSAGE = "Customer tidak ditemukan";
    private static final String FALSE_CREDENTIALS = "Nomor telepon atau password salah";

    public ResponseEntity<BaseResponse<CustomerAuthResponse>> login(
            CustomerLoginRequest request,
            String message
    ) {
        Customer customer = customerRepository
                .findByPhoneNumberAndDeletedDateIsNull(
                        request.getPhoneNumber()
                )
                .orElseThrow(() ->
                        new AuthenticationException(
                                FALSE_CREDENTIALS
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                customer.getPassword()
        )) {
            throw new AuthenticationException(
                    FALSE_CREDENTIALS
            );
        }

        if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
            customerDeviceService.register(
                    customer,
                    new com.bagas.pinjam100.dto.notification.RegisterDeviceRequest(
                            request.getFcmToken()
                    )
            );
        }

        return createAuthResponse(customer, message);
    }

    public ResponseEntity<Void> logout(String token) {
        var jwt = jwtService.parse(token);
        String phoneNumber = jwt.getSubject();
        Instant expiresAt = jwtService.getExpiration(token);

        Customer customer = customerRepository
                .findByPhoneNumberAndDeletedDateIsNull(phoneNumber)
                .orElseThrow(() ->
                        new EntityNotFoundException(NOT_FOUND_MESSAGE)
                );

        customer.setLogoutDate(
                LocalDateTime.now(ZoneId.of("Asia/Jakarta"))
        );
        customerRepository.save(customer);

        tokenBlacklistService.revoke(token, expiresAt);

        return ResponseEntity.noContent().build();
    }

    public Customer getCurrentCustomer(UUID customerId) {
        return customerRepository
                .findByIdAndDeletedDateIsNull(customerId)
                .orElseThrow(() ->
                        new AuthenticationException(
                                NOT_FOUND_MESSAGE
                        )
                );
    }

    @Transactional
    public ResponseEntity<BaseResponse<Void>> register(
            CustomerRequest request
    ) {
        if (customerRepository.existsByPhoneNumberAndDeletedDateIsNull(
                request.getPhoneNumber()
        )) {
            throw new ConflictException(
                    "Nomor telepon sudah terdaftar"
            );
        }

        if (customerRepository.existsByEmailAndDeletedDateIsNull(
                request.getEmail()
        )) {
            throw new ConflictException(
                    "Email sudah terdaftar"
            );
        }

        Customer customer = new Customer();

        String customerNumber = "CUS-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();

        customer.setCustomerNumber(customerNumber);
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        customerRepository.save(customer);

        otpVerificationService.generate(
                customer.getPhoneNumber()
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Registrasi berhasil"
                )
        );
    }

    @Transactional
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> verifyOtp(
            VerifyOtpRequest request
    ) {
        otpVerificationService.verify(
                request.getPhoneNumber(),
                request.getOtpCode()
        );

        Customer customer = customerRepository
                .findByPhoneNumberAndDeletedDateIsNull(
                        request.getPhoneNumber()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                NOT_FOUND_MESSAGE
                        )
                );

        Instant now = Instant.now();

        customer.setLoginDate(
                LocalDateTime.now(ZoneId.of("Asia/Jakarta"))
        );

        customerRepository.save(customer);

        String token = jwtService.issueCustomer(
                customer,
                now
        );

        Instant expiresAt = jwtService.getExpiration(token);

        CustomerAuthUserResponse user = new CustomerAuthUserResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.isProfileCompleted()
        );

        CustomerAuthResponse response = new CustomerAuthResponse(
                token,
                user,
                expiresAt.toEpochMilli()
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "OTP berhasil diverifikasi",
                        response
                )
        );
    }

    public ResponseEntity<BaseResponse<Void>> resendOtp(
            ResendOtpRequest request
    ) {
        Customer customer = customerRepository
                .findByPhoneNumberAndDeletedDateIsNull(
                        request.getPhoneNumber()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                NOT_FOUND_MESSAGE
                        )
                );

        if (customer.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new ConflictException(
                    "Akun sudah terverifikasi"
            );
        }

        otpVerificationService.generate(
                customer.getPhoneNumber()
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "OTP berhasil dikirim ulang"
                )
        );
    }

    public ResponseEntity<BaseResponse<Void>> changePassword(
            String token,
            ChangePasswordRequest request
    ) {
        String phoneNumber = jwtService.getUsername(token);

        Customer customer = customerRepository
                .findByPhoneNumberAndDeletedDateIsNull(phoneNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer tidak ditemukan"
                        )
                );

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                customer.getPassword()
        )) {
            throw new RuntimeException(
                    "Password saat ini salah"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                customer.getPassword()
        )) {
            throw new RuntimeException(
                    "Password baru tidak boleh sama dengan password lama"
            );
        }

        customer.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        customerRepository.save(customer);

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Password berhasil diubah"
                )
        );
    }

    private ResponseEntity<BaseResponse<CustomerAuthResponse>> createAuthResponse(
            Customer customer,
            String message
    ) {
        Instant now = Instant.now();

        customer.setLoginDate(
                LocalDateTime.now(ZoneId.of("Asia/Jakarta"))
        );

        customerRepository.save(customer);

        String token = jwtService.issueCustomer(
                customer,
                now
        );

        Instant expiresAt = jwtService.getExpiration(token);

        CustomerAuthUserResponse user = new CustomerAuthUserResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.isProfileCompleted()
        );

        CustomerAuthResponse response = new CustomerAuthResponse(
                token,
                user,
                expiresAt.toEpochMilli()
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        message,
                        response
                )
        );
    }
}