package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.AuthResponse;
import com.bagas.pinjam100.dto.auth.LoginRequest;
import com.bagas.pinjam100.dto.auth.LogoutRequest;
import com.bagas.pinjam100.dto.auth.RefreshTokenRequest;
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
    public ResponseEntity<AuthResponse> loginUser(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(
                appUserDetailsService.findUser(
                        request.getIdentityNumber()
                ),
                request
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody LogoutRequest request
    ) {
        String token = authorization.substring(7);

        return authService.logout(
                token,
                request.getRefreshToken()
        );
    }

    @GetMapping("/get-current-user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(
                new UserResponse(
                        authService.getCurrentUser()
                )
        );
    }
}