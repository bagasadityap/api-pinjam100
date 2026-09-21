package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.*;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.entity.otp.ResendOtpRequest;
import com.bagas.pinjam100.entity.otp.VerifyOtpRequest;
import com.bagas.pinjam100.service.auth.CustomerAuthService;
import com.bagas.pinjam100.service.auth.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth/customer", "/auth/customer/"})
@Tag(
        name = "Customer Authentication",
        description = "Authentication management operations for customers"
)
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;
    private final PasswordResetService passwordResetService;

    public CustomerAuthController(CustomerAuthService customerAuthService, PasswordResetService passwordResetService) {
        this.customerAuthService = customerAuthService;
        this.passwordResetService = passwordResetService;
    }

    @Operation(
            summary = "Login customer",
            description = "Authenticate customer using phone number or email and password"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data request tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials or unverified customer",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Nomor HP atau password salah\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> loginCustomer(
            @Valid @RequestBody CustomerLoginRequest request
    ) {
        return customerAuthService.login(request, "Login berhasil");
    }

    @Operation(
            summary = "Register customer",
            description = "Register a new customer account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registration initiated, OTP sent"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data registrasi tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Nomor HP atau email sudah terdaftar\",\"error\":\"Conflict\",\"status\":409,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> registerCustomer(
            @Valid @RequestBody CustomerRequest request
    ) {
        return customerAuthService.register(request);
    }

    @Operation(
            summary = "Verify OTP",
            description = "Verify OTP code sent to customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP verified successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired OTP code",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Kode OTP tidak valid atau sudah kedaluwarsa\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Customer tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        return customerAuthService.verifyOtp(request);
    }

    @Operation(
            summary = "Resend OTP",
            description = "Resend a new OTP code to customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP resent successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid resend request or rate limit exceeded",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Harap tunggu beberapa saat sebelum meminta OTP baru\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Customer tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping("/resend-otp")
    public ResponseEntity<BaseResponse<Void>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request
    ) {
        return customerAuthService.resendOtp(request);
    }

    @Operation(
            summary = "Refresh token",
            description = "Generate new access token for customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token request",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Refresh token tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token expired or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Refresh token telah kedaluwarsa\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return customerAuthService.refreshToken(request);
    }

    @Operation(
            summary = "Logout customer",
            description = "Invalidate current customer session and tokens"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid logout request",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data logout tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
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

    @Operation(
            summary = "Change password",
            description = "Change customer password while authenticated"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or old password incorrect",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Password lama tidak sesuai\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
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

    @Operation(
            summary = "Forgot password request",
            description = "Send reset password instructions/link to customer email"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reset password request submitted"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Format email tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Email not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Email tidak terdaftar\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
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

    @Operation(
            summary = "Reset password",
            description = "Reset password using reset token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Token reset password tidak valid atau sudah kedaluwarsa\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
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