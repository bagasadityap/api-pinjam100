package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.*;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.entity.auth.CustomerRefreshToken;
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
    private final CustomerRegistrationService customerRegistrationService;
    private final CustomerRefreshTokenService customerRefreshTokenService;

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

    public ResponseEntity<Void> logout(
            String token,
            String refreshToken
    ) {
        var jwt = jwtService.parse(token);

        String phoneNumber = jwt.getSubject();

        Instant expiresAt =
                jwtService.getExpiration(token);

        Customer customer =
                customerRepository
                        .findByPhoneNumberAndDeletedDateIsNull(
                                phoneNumber
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        NOT_FOUND_MESSAGE
                                )
                        );

        customer.setLogoutDate(
                LocalDateTime.now(
                        ZoneId.of("Asia/Jakarta")
                )
        );

        customerRepository.save(customer);

        tokenBlacklistService.revoke(
                token,
                expiresAt
        );

        customerRefreshTokenService.revoke(
                refreshToken
        );

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

        if (customerRepository.existsByNationalIdAndDeletedDateIsNull(
                request.getNationalId()
        )) {
            throw new ConflictException(
                    "NIK sudah terdaftar"
            );
        }

        PendingCustomerRegistration registration =
                new PendingCustomerRegistration(
                        request.getFullName(),
                        request.getNationalId(),
                        request.getEmail(),
                        request.getPhoneNumber(),
                        passwordEncoder.encode(request.getPassword())
                );

        customerRegistrationService.save(registration);

        otpVerificationService.generate(
                registration.getPhoneNumber()
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Registrasi berhasil",
                        null
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

        PendingCustomerRegistration registration =
                customerRegistrationService.get(
                        request.getPhoneNumber()
                );

        if (registration == null) {
            throw new EntityNotFoundException(
                    "Data registrasi sudah kedaluwarsa"
            );
        }

        if (customerRepository.existsByPhoneNumberAndDeletedDateIsNull(
                registration.getPhoneNumber()
        )) {
            throw new ConflictException(
                    "Nomor telepon sudah terdaftar"
            );
        }

        if (customerRepository.existsByEmailAndDeletedDateIsNull(
                registration.getEmail()
        )) {
            throw new ConflictException(
                    "Email sudah terdaftar"
            );
        }

        Customer customer = new Customer();

        String customerNumber = "CUS-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();

        customer.setCustomerNumber(customerNumber);
        customer.setNationalId(registration.getNationalId());
        customer.setFullName(registration.getFullName());
        customer.setEmail(registration.getEmail());
        customer.setPhoneNumber(registration.getPhoneNumber());
        customer.setPassword(registration.getPassword());
        customer.setLoginDate(
                LocalDateTime.now(
                        ZoneId.of("Asia/Jakarta")
                )
        );
        customer.setVerificationStatus(
                VerificationStatus.PENDING
        );

        customerRepository.save(customer);

        customerRegistrationService.delete(
                registration.getPhoneNumber()
        );

        Instant now = Instant.now();

        String token = jwtService.issueCustomer(
                customer,
                now
        );

        Instant expiresAt = jwtService.getExpiration(token);

        String refreshToken =
                customerRefreshTokenService.create(customer);

        CustomerAuthUserResponse user =
                new CustomerAuthUserResponse(
                        customer.getId(),
                        customer.getCustomerNumber(),
                        customer.getFullName(),
                        customer.getEmail(),
                        customer.getPhoneNumber(),
                        customer.isProfileCompleted()
                );

        CustomerAuthResponse response =
                new CustomerAuthResponse(
                        token,
                        refreshToken,
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
        PendingCustomerRegistration registration =
                customerRegistrationService.get(
                        request.getPhoneNumber()
                );

        if (registration == null) {
            throw new EntityNotFoundException(
                    "Data registrasi tidak ditemukan atau sudah kedaluwarsa"
            );
        }

        otpVerificationService.generate(
                registration.getPhoneNumber()
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "OTP berhasil dikirim ulang",
                        null
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
                        "Password berhasil diubah",
                        null
                )
        );
    }

    public ResponseEntity<BaseResponse<CustomerAuthResponse>> refreshToken(
            RefreshTokenRequest request
    ) {
        CustomerRefreshToken refreshToken =
                customerRefreshTokenService.validate(
                        request.getRefreshToken()
                );

        Customer customer = refreshToken.getCustomer();

        Instant now = Instant.now();

        String token = jwtService.issueCustomer(
                customer,
                now
        );

        Instant expiresAt = jwtService.getExpiration(token);

        CustomerAuthUserResponse user =
                new CustomerAuthUserResponse(
                        customer.getId(),
                        customer.getCustomerNumber(),
                        customer.getFullName(),
                        customer.getEmail(),
                        customer.getPhoneNumber(),
                        customer.isProfileCompleted()
                );

        CustomerAuthResponse response =
                new CustomerAuthResponse(
                        token,
                        request.getRefreshToken(),
                        user,
                        expiresAt.toEpochMilli()
                );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Token berhasil diperbarui",
                        response
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

        String refreshToken =
                customerRefreshTokenService.create(customer);

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
                refreshToken,
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