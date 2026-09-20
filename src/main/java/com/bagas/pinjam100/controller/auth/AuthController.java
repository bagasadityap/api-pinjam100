package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.AuthResponse;
import com.bagas.pinjam100.dto.auth.LoginRequest;
import com.bagas.pinjam100.dto.auth.LogoutRequest;
import com.bagas.pinjam100.dto.auth.RefreshTokenRequest;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.response.userrolepermission.UserResponse;
import com.bagas.pinjam100.service.auth.AppUserDetailsService;
import com.bagas.pinjam100.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/auth", "/auth/"})
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
                        "Logout berhasil"
                )
        );
    }

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