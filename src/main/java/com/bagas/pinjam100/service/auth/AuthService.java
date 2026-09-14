package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.AuthResponse;
import com.bagas.pinjam100.dto.auth.LoginRequest;
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

    public ResponseEntity<AuthResponse> login(Optional<AppUser> found, LoginRequest request) {
        if (found.isEmpty() || !passwordEncoder.matches(request.getPassword(), found.get().getPassword())) {
            throw new AuthenticationException(
                    "NIP atau password salah"
            );
        }

        AppUser user = found.get();
        if (user.getRole() == null) {
            throw new AuthenticationException("User belum memiliki role");
        }
        if (!user.isEnabled()) {
            throw new AuthenticationException("Status user tidak aktif, mohon menghungi administrator");
        }

        String token = jwtService.issue(user, Instant.now());

        List<String> permissions = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_"))
                .toList();

        AuthResponse response = new AuthResponse(
                token,
                user.getIdentityNumber(),
                user.getRole().getRoleName(),
                permissions
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Void> logout(String token) {
        Instant expiresAt = jwtService.getExpiration(token);
        tokenBlacklistService.revoke(token, expiresAt);

        return ResponseEntity.noContent().build();
    }

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AppUser appUser)) {
            throw new AuthenticationException("User belum terautentikasi");
        }

        return userRepository.findByIdAndDeletedDateIsNull(appUser.getIdUser())
                .orElseThrow(() ->
                        new EntityNotFoundException("User tidak ditemukan")
                );
    }
}