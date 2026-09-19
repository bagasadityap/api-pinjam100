package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.*;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.entity.otp.ResendOtpRequest;
import com.bagas.pinjam100.entity.otp.VerifyOtpRequest;
import com.bagas.pinjam100.service.auth.CustomerAuthService;
import com.bagas.pinjam100.service.auth.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth/customer", "/auth/customer/"})
public class CustomerAuthController {
    private final CustomerAuthService customerAuthService;
    private final PasswordResetService passwordResetService;

    public CustomerAuthController(CustomerAuthService customerAuthService, PasswordResetService passwordResetService) {
        this.customerAuthService = customerAuthService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> loginCustomer(
            @Valid @RequestBody CustomerLoginRequest request
    ) {
        return customerAuthService.login(request, "Login berhasil");
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> registerCustomer(
            @Valid @RequestBody CustomerRequest request
    ) {
        return customerAuthService.register(request);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        return customerAuthService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<BaseResponse<Void>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request
    ) {
        return customerAuthService.resendOtp(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return customerAuthService.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody LogoutRequest request
    ) {
        String token = authorization.substring(7);

        return customerAuthService.logout(
                token,
                request.getRefreshToken()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String token = authorization.substring(7);

        return customerAuthService.changePassword(
                token,
                request
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.requestReset(request.getEmail());

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Password berhasil diubah",
                        null
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(request);

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Password berhasil diubah",
                        null
                )
        );
    }
}