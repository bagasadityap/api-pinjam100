package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.dto.auth.AuthResponse;
import com.bagas.pinjam100.dto.auth.LoginRequest;
import com.bagas.pinjam100.dto.auth.RefreshTokenRequest;
import com.bagas.pinjam100.entity.auth.UserRefreshToken;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.exception.AuthenticationException;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import com.bagas.pinjam100.security.AppUser;
import com.bagas.pinjam100.service.jwt.JwtService;
import com.bagas.pinjam100.service.jwt.TokenBlacklistService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceTest")
class AuthServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String IDENTITY_NUMBER = "1234567890";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encoded_password123";
    private static final String TOKEN = "mocked-jwt-token";
    private static final String REFRESH_TOKEN = "mocked-refresh-token";
    private static final String ROLE_NAME = "ADMIN";

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRefreshTokenService userRefreshTokenService;

    @Mock
    private AppUserDetailsService appUserDetailsService;

    @InjectMocks
    private AuthService authService;

    // =========================================================
    // LOGIN
    // =========================================================

    @Nested
    @DisplayName("login")
    class LoginTest {

        @Test
        @DisplayName("should login successfully when credentials and state are valid")
        void shouldLoginSuccessfully() {
            LoginRequest request = createLoginRequest();
            AppUser appUser = createAppUser(true, true);
            User userEntity = createUserEntity();

            when(passwordEncoder.matches(request.getPassword(), appUser.getPassword()))
                    .thenReturn(true);
            when(jwtService.issue(eq(appUser), any(Instant.class)))
                    .thenReturn(TOKEN);
            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(userEntity));
            when(userRefreshTokenService.create(userEntity))
                    .thenReturn(REFRESH_TOKEN);

            AuthResponse response = authService.login(Optional.of(appUser), request);

            assertNotNull(response);
            assertEquals(TOKEN, response.getToken());
            assertEquals(REFRESH_TOKEN, response.getRefreshToken());
            assertEquals(IDENTITY_NUMBER, response.getIdentityNumber());
            assertEquals(ROLE_NAME, response.getRole());
            assertEquals(List.of("READ_DATA", "WRITE_DATA"), response.getPermissions());

            verify(passwordEncoder).matches(request.getPassword(), appUser.getPassword());
            verify(jwtService).issue(eq(appUser), any(Instant.class));
            verify(userRepository).findByIdAndDeletedDateIsNull(USER_ID);
            verify(userRefreshTokenService).create(userEntity);
        }

        @Test
        @DisplayName("should throw AuthenticationException when user is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            LoginRequest request = createLoginRequest();

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.login(Optional.empty(), request)
            );

            assertEquals("NIP atau password salah", exception.getMessage());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("should throw AuthenticationException when password does not match")
        void shouldThrowExceptionWhenPasswordInvalid() {
            LoginRequest request = createLoginRequest();
            AppUser appUser = createAppUser(true, true);

            when(passwordEncoder.matches(request.getPassword(), appUser.getPassword()))
                    .thenReturn(false);

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.login(Optional.of(appUser), request)
            );

            assertEquals("NIP atau password salah", exception.getMessage());
            verify(passwordEncoder).matches(request.getPassword(), appUser.getPassword());
            verify(jwtService, never()).issue(any(), any());
        }

        @Test
        @DisplayName("should throw AuthenticationException when user has no role")
        void shouldThrowExceptionWhenRoleIsNull() {
            LoginRequest request = createLoginRequest();
            AppUser appUser = createAppUser(false, true);

            when(passwordEncoder.matches(request.getPassword(), appUser.getPassword()))
                    .thenReturn(true);

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.login(Optional.of(appUser), request)
            );

            assertEquals("User belum memiliki role", exception.getMessage());
            verify(jwtService, never()).issue(any(), any());
        }

        @Test
        @DisplayName("should throw AuthenticationException when user is disabled")
        void shouldThrowExceptionWhenUserIsDisabled() {
            LoginRequest request = createLoginRequest();
            AppUser appUser = createAppUser(true, false);

            when(passwordEncoder.matches(request.getPassword(), appUser.getPassword()))
                    .thenReturn(true);

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.login(Optional.of(appUser), request)
            );

            assertEquals("Status user tidak aktif, mohon menghungi administrator", exception.getMessage());
            verify(jwtService, never()).issue(any(), any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user entity not found in repository")
        void shouldThrowExceptionWhenUserEntityNotFound() {
            LoginRequest request = createLoginRequest();
            AppUser appUser = createAppUser(true, true);

            when(passwordEncoder.matches(request.getPassword(), appUser.getPassword()))
                    .thenReturn(true);
            when(jwtService.issue(eq(appUser), any(Instant.class)))
                    .thenReturn(TOKEN);
            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> authService.login(Optional.of(appUser), request)
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
            verify(userRefreshTokenService, never()).create(any());
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    @Nested
    @DisplayName("logout")
    class LogoutTest {

        @Test
        @DisplayName("should revoke tokens and return 204 No Content")
        void shouldLogoutSuccessfully() {
            Instant expiresAt = Instant.now().plusSeconds(3600);

            when(jwtService.getExpiration(TOKEN)).thenReturn(expiresAt);

            ResponseEntity<Void> response = authService.logout(TOKEN, REFRESH_TOKEN);

            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            verify(jwtService).getExpiration(TOKEN);
            verify(tokenBlacklistService).revoke(TOKEN, expiresAt);
            verify(userRefreshTokenService).revoke(REFRESH_TOKEN);
        }
    }

    // =========================================================
    // GET CURRENT USER
    // =========================================================

    @Nested
    @DisplayName("getCurrentUser")
    class GetCurrentUserTest {

        @AfterEach
        void tearDown() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("should return current logged in user entity successfully")
        void shouldReturnCurrentUserSuccessfully() {
            AppUser appUser = createAppUser(true, true);
            User userEntity = createUserEntity();

            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(appUser);
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(userEntity));

            User result = authService.getCurrentUser();

            assertNotNull(result);
            assertEquals(USER_ID, result.getId());

            verify(userRepository).findByIdAndDeletedDateIsNull(USER_ID);
        }

        @Test
        @DisplayName("should throw AuthenticationException when authentication in context is null")
        void shouldThrowExceptionWhenAuthenticationIsNull() {
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);
            SecurityContextHolder.setContext(securityContext);

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.getCurrentUser()
            );

            assertEquals("User belum terautentikasi", exception.getMessage());
            verify(userRepository, never()).findByIdAndDeletedDateIsNull(any());
        }

        @Test
        @DisplayName("should throw AuthenticationException when principal is not instance of AppUser")
        void shouldThrowExceptionWhenPrincipalIsNotAppUser() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("anonymousUser");
            SecurityContextHolder.setContext(securityContext);

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.getCurrentUser()
            );

            assertEquals("User belum terautentikasi", exception.getMessage());
            verify(userRepository, never()).findByIdAndDeletedDateIsNull(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user in context not found in repository")
        void shouldThrowExceptionWhenUserNotInRepository() {
            AppUser appUser = createAppUser(true, true);
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(appUser);
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> authService.getCurrentUser()
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
            verify(userRepository).findByIdAndDeletedDateIsNull(USER_ID);
        }
    }

    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTest {

        @Test
        @DisplayName("should refresh token successfully")
        void shouldRefreshTokenSuccessfully() {
            RefreshTokenRequest request = new RefreshTokenRequest(REFRESH_TOKEN);
            User userEntity = createUserEntity();
            UserRefreshToken userRefreshToken = new UserRefreshToken();
            userRefreshToken.setUser(userEntity);

            AppUser appUser = createAppUser(true, true);

            when(userRefreshTokenService.validate(REFRESH_TOKEN))
                    .thenReturn(userRefreshToken);
            when(appUserDetailsService.findUser(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(appUser));
            when(jwtService.issue(eq(appUser), any(Instant.class)))
                    .thenReturn("new-access-token");

            AuthResponse response = authService.refreshToken(request);

            assertNotNull(response);
            assertEquals("new-access-token", response.getToken());
            assertEquals(REFRESH_TOKEN, response.getRefreshToken());
            assertEquals(IDENTITY_NUMBER, response.getIdentityNumber());
            assertEquals(ROLE_NAME, response.getRole());
            assertEquals(List.of("READ_DATA", "WRITE_DATA"), response.getPermissions());

            verify(userRefreshTokenService).validate(REFRESH_TOKEN);
            verify(appUserDetailsService).findUser(IDENTITY_NUMBER);
            verify(jwtService).issue(eq(appUser), any(Instant.class));
        }

        @Test
        @DisplayName("should throw AuthenticationException when AppUser detail is empty")
        void shouldThrowExceptionWhenAppUserNotFound() {
            RefreshTokenRequest request = new RefreshTokenRequest(REFRESH_TOKEN);
            User userEntity = createUserEntity();
            UserRefreshToken userRefreshToken = new UserRefreshToken();
            userRefreshToken.setUser(userEntity);

            when(userRefreshTokenService.validate(REFRESH_TOKEN))
                    .thenReturn(userRefreshToken);
            when(appUserDetailsService.findUser(IDENTITY_NUMBER))
                    .thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.refreshToken(request)
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
            verify(jwtService, never()).issue(any(), any());
        }

        @Test
        @DisplayName("should throw AuthenticationException when AppUser role is null during refresh")
        void shouldThrowExceptionWhenRoleIsNullOnRefresh() {
            RefreshTokenRequest request = new RefreshTokenRequest(REFRESH_TOKEN);
            User userEntity = createUserEntity();
            UserRefreshToken userRefreshToken = new UserRefreshToken();
            userRefreshToken.setUser(userEntity);

            AppUser appUser = createAppUser(false, true);

            when(userRefreshTokenService.validate(REFRESH_TOKEN))
                    .thenReturn(userRefreshToken);
            when(appUserDetailsService.findUser(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(appUser));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.refreshToken(request)
            );

            assertEquals("User belum memiliki role", exception.getMessage());
            verify(jwtService, never()).issue(any(), any());
        }

        @Test
        @DisplayName("should throw AuthenticationException when AppUser is disabled during refresh")
        void shouldThrowExceptionWhenUserDisabledOnRefresh() {
            RefreshTokenRequest request = new RefreshTokenRequest(REFRESH_TOKEN);
            User userEntity = createUserEntity();
            UserRefreshToken userRefreshToken = new UserRefreshToken();
            userRefreshToken.setUser(userEntity);

            AppUser appUser = createAppUser(true, false);

            when(userRefreshTokenService.validate(REFRESH_TOKEN))
                    .thenReturn(userRefreshToken);
            when(appUserDetailsService.findUser(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(appUser));

            AuthenticationException exception = assertThrows(
                    AuthenticationException.class,
                    () -> authService.refreshToken(request)
            );

            assertEquals("Status user tidak aktif, mohon menghungi administrator", exception.getMessage());
            verify(jwtService, never()).issue(any(), any());
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setIdentityNumber(IDENTITY_NUMBER);
        request.setPassword(RAW_PASSWORD);
        return request;
    }

    private User createUserEntity() {
        User user = new User();
        user.setId(USER_ID);
        user.setIdentityNumber(IDENTITY_NUMBER);
        return user;
    }

    private AppUser createAppUser(boolean hasRole, boolean enabled) {
        Role role = null;
        if (hasRole) {
            role = new Role();
            role.setRoleName(ROLE_NAME);
        }

        return new AppUser(
                USER_ID,
                IDENTITY_NUMBER,
                ENCODED_PASSWORD,
                enabled,
                role,
                null, // Parameter Branch (bisa passing null jika tidak dipakai di test ini)
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("READ_DATA"),
                        new SimpleGrantedAuthority("WRITE_DATA")
                )
        );
    }
}
