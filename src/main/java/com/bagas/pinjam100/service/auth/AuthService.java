package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.AuthResponse;
import com.bagas.pinjam100.dto.auth.LoginRequest;
import com.bagas.pinjam100.dto.auth.RefreshTokenRequest;
import com.bagas.pinjam100.entity.auth.UserRefreshToken;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import com.bagas.pinjam100.security.AppUser;
import com.bagas.pinjam100.service.jwt.JwtService;
import com.bagas.pinjam100.service.jwt.TokenBlacklistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final UserRefreshTokenService userRefreshTokenService;
    private final AppUserDetailsService appUserDetailsService;

    public AuthResponse login(
            Optional<AppUser> found,
            LoginRequest request
    ) {
        if (found.isEmpty() ||
                !passwordEncoder.matches(
                        request.getPassword(),
                        found.get().getPassword()
                )) {

            throw new AuthenticationException(
                    "NIP atau password salah"
            );
        }

        AppUser user = found.get();

        if (user.getRole() == null) {
            throw new AuthenticationException(
                    "User belum memiliki role"
            );
        }

        if (!user.isEnabled()) {
            throw new AuthenticationException(
                    "Status user tidak aktif, mohon menghungi administrator"
            );
        }

        Instant now = Instant.now();

        String token = jwtService.issue(
                user,
                now
        );

        User userEntity = userRepository
                .findByIdAndDeletedDateIsNull(
                        user.getIdUser()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tidak ditemukan"
                        )
                );

        String refreshToken =
                userRefreshTokenService.create(
                        userEntity
                );

        List<String> permissions = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority ->
                        !authority.startsWith("ROLE_")
                )
                .toList();

        return new AuthResponse(
                token,
                refreshToken,
                user.getIdentityNumber(),
                user.getRole().getRoleName(),
                permissions
        );
    }

    public ResponseEntity<Void> logout(
            String token,
            String refreshToken
    ) {
        Instant expiresAt =
                jwtService.getExpiration(token);

        tokenBlacklistService.revoke(
                token,
                expiresAt
        );

        userRefreshTokenService.revoke(
                refreshToken
        );

        return ResponseEntity.noContent().build();
    }

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof AppUser appUser)) {

            throw new AuthenticationException(
                    "User belum terautentikasi"
            );
        }

        return userRepository
                .findByIdAndDeletedDateIsNull(
                        appUser.getIdUser()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tidak ditemukan"
                        )
                );
    }

    public AuthResponse refreshToken(
            RefreshTokenRequest request
    ) {
        UserRefreshToken refreshToken =
                userRefreshTokenService.validate(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        Optional<AppUser> found =
                appUserDetailsService.findUser(
                        user.getIdentityNumber()
                );

        if (found.isEmpty()) {
            throw new AuthenticationException(
                    "User tidak ditemukan"
            );
        }

        AppUser appUser = found.get();

        if (appUser.getRole() == null) {
            throw new AuthenticationException(
                    "User belum memiliki role"
            );
        }

        if (!appUser.isEnabled()) {
            throw new AuthenticationException(
                    "Status user tidak aktif, mohon menghungi administrator"
            );
        }

        String token = jwtService.issue(
                appUser,
                Instant.now()
        );

        List<String> permissions = appUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority ->
                        !authority.startsWith("ROLE_")
                )
                .toList();

        return new AuthResponse(
                token,
                request.getRefreshToken(),
                appUser.getIdentityNumber(),
                appUser.getRole().getRoleName(),
                permissions
        );
    }
}