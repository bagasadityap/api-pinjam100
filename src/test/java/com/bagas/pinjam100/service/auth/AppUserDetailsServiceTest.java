package com.bagas.pinjam100.service.auth;

import com.bagas.pinjam100.entity.userrolepermission.Permission;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.entity.userrolepermission.RolePermission;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import com.bagas.pinjam100.security.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserDetailsServiceTest")
class AppUserDetailsServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String IDENTITY_NUMBER = "1234567890";
    private static final String UNKNOWN_IDENTITY = "9999999999";
    private static final String PASSWORD = "encoded_password";
    private static final String ROLE_NAME = "ADMIN";
    private static final String PERMISSION_NAME = "READ_DATA";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsernameTest {

        @Test
        @DisplayName("should load AppUser successfully when user exists and has valid password")
        void shouldLoadUserByUsernameSuccessfully() {
            User user = createUserEntity(true, true, true);

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(user));

            AppUser result = appUserDetailsService.loadUserByUsername(IDENTITY_NUMBER);

            assertNotNull(result);
            assertEquals(USER_ID, result.getIdUser());
            assertEquals(IDENTITY_NUMBER, result.getIdentityNumber());
            assertEquals(PASSWORD, result.getPassword());
            assertTrue(result.isEnabled());
            assertNotNull(result.getRole());
            assertEquals(ROLE_NAME, result.getRole().getRoleName());

            List<String> authorities = result.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            assertTrue(authorities.contains("ROLE_" + ROLE_NAME));
            assertTrue(authorities.contains(PERMISSION_NAME));

            verify(userRepository).findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER);
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findByIdentityNumberWithRoleAndPermissions(UNKNOWN_IDENTITY))
                    .thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> appUserDetailsService.loadUserByUsername(UNKNOWN_IDENTITY)
            );

            assertEquals("Pengguna tidak dikenal: " + UNKNOWN_IDENTITY, exception.getMessage());
            verify(userRepository).findByIdentityNumberWithRoleAndPermissions(UNKNOWN_IDENTITY);
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user password is null")
        void shouldThrowExceptionWhenPasswordIsNull() {
            User user = createUserEntity(false, true, true);

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(user));

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> appUserDetailsService.loadUserByUsername(IDENTITY_NUMBER)
            );

            assertEquals("Pengguna tidak dikenal: " + IDENTITY_NUMBER, exception.getMessage());
            verify(userRepository).findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER);
        }
    }

    @Nested
    @DisplayName("findUser")
    class FindUserTest {

        @Test
        @DisplayName("should return optional AppUser when user exists")
        void shouldReturnOptionalAppUserWhenUserExists() {
            User user = createUserEntity(true, true, true);

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(user));

            Optional<AppUser> result = appUserDetailsService.findUser(IDENTITY_NUMBER);

            assertTrue(result.isPresent());
            assertEquals(IDENTITY_NUMBER, result.get().getIdentityNumber());
            verify(userRepository).findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER);
        }

        @Test
        @DisplayName("should return empty optional when user password is null")
        void shouldReturnEmptyWhenPasswordIsNull() {
            User user = createUserEntity(false, true, true);

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(user));

            Optional<AppUser> result = appUserDetailsService.findUser(IDENTITY_NUMBER);

            assertTrue(result.isEmpty());
            verify(userRepository).findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER);
        }

        @Test
        @DisplayName("should map authorities correctly when role and rolePermissions are null or partially null")
        void shouldMapAuthoritiesCorrectlyWhenRoleOrPermissionsAreNull() {
            User userWithNullRole = createUserEntity(true, false, false);

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(userWithNullRole));

            Optional<AppUser> result = appUserDetailsService.findUser(IDENTITY_NUMBER);

            assertTrue(result.isPresent());
            assertNull(result.get().getRole());
            assertTrue(result.get().getAuthorities().isEmpty());
        }

        @Test
        @DisplayName("should map authorities correctly when role exists but permissions are empty or null")
        void shouldMapAuthoritiesCorrectlyWhenPermissionsEmpty() {
            User userWithoutPermissions = createUserEntity(true, true, false);

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(userWithoutPermissions));

            Optional<AppUser> result = appUserDetailsService.findUser(IDENTITY_NUMBER);

            assertTrue(result.isPresent());
            assertEquals(1, result.get().getAuthorities().size());
            assertEquals("ROLE_" + ROLE_NAME, result.get().getAuthorities().iterator().next().getAuthority());
        }

        @Test
        @DisplayName("should handle rolePermission or permission being null safely")
        void shouldHandleNullRolePermissionOrPermissionSafely() {
            User user = createUserEntity(true, true, false);
            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermission(null);
            user.getRole().setRolePermissions(Set.of(rolePermission));

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(user));

            Optional<AppUser> result = appUserDetailsService.findUser(IDENTITY_NUMBER);

            assertTrue(result.isPresent());
            assertEquals(1, result.get().getAuthorities().size());
        }

        @Test
        @DisplayName("should set status false when user status is null or false")
        void shouldSetStatusFalseWhenUserStatusIsFalseOrNull() {
            User user = createUserEntity(true, true, true);
            user.setStatus(null);

            when(userRepository.findByIdentityNumberWithRoleAndPermissions(IDENTITY_NUMBER))
                    .thenReturn(Optional.of(user));

            Optional<AppUser> result = appUserDetailsService.findUser(IDENTITY_NUMBER);

            assertTrue(result.isPresent());
            assertFalse(result.get().isEnabled());
        }
    }

    private User createUserEntity(boolean hasPassword, boolean hasRole, boolean hasPermissions) {
        User user = new User();
        user.setId(USER_ID);
        user.setIdentityNumber(IDENTITY_NUMBER);
        user.setPassword(hasPassword ? PASSWORD : null);
        user.setStatus(true);

        if (hasRole) {
            Role role = new Role();
            role.setRoleName(ROLE_NAME);

            if (hasPermissions) {
                Permission permission = new Permission();
                permission.setPermissionName(PERMISSION_NAME);

                RolePermission rolePermission = new RolePermission();
                rolePermission.setPermission(permission);

                role.setRolePermissions(Set.of(rolePermission));
            }

            user.setRole(role);
        }

        return user;
    }
}