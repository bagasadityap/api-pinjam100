package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.AuthResponse;
import com.bagas.pinjam100.dto.auth.LoginRequest;
import com.bagas.pinjam100.dto.auth.LogoutRequest;
import com.bagas.pinjam100.dto.auth.RefreshTokenRequest;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.response.userrolepermission.UserResponse;
import com.bagas.pinjam100.service.auth.AppUserDetailsService;
import com.bagas.pinjam100.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/auth", "/auth/"})
@Tag(
        name = "Authentication",
        description = "Authentication management operations"
)
public class AuthController {

    private final AuthService authService;
    private final AppUserDetailsService appUserDetailsService;

    public AuthController(
            AuthService authService,
            AppUserDetailsService appUserDetailsService
    ) {
        this.authService = authService;
        this.appUserDetailsService = appUserDetailsService;
    }

    @Operation(
            summary = "Login user",
            description = "Authenticate user using identity number and password"
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
                    description = "Invalid identity number or password",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"NIP atau password salah\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> loginUser(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(
                appUserDetailsService.findUser(
                        request.getIdentityNumber()
                ),
                request
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Login berhasil",
                        response
                )
        );
    }

    @Operation(
            summary = "Refresh token",
            description = "Generate new access token using refresh token"
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
    public ResponseEntity<BaseResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Token berhasil diperbarui",
                        response
                )
        );
    }

    @Operation(
            summary = "Logout user",
            description = "Invalidate current session and tokens"
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
    public ResponseEntity<BaseResponse<Void>> logout(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody LogoutRequest request
    ) {
        String token = authorization.substring(7);

        authService.logout(
                token,
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                BaseResponse.success(
                        "Logout berhasil",
                        null
                )
        );
    }

    @Operation(
            summary = "Get current user",
            description = "Retrieve details of currently authenticated user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user data retrieved successfully"
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @GetMapping("/get-current-user")
    public ResponseEntity<BaseResponse<UserResponse>> getCurrentUser() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Berhasil mendapatkan data user",
                        new UserResponse(
                                authService.getCurrentUser()
                        )
                )
        );
    }
}