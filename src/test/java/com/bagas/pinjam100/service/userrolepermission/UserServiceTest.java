package com.bagas.pinjam100.service.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.UserRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.UserResponse;
import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.exception.ConflictException;
import com.bagas.pinjam100.repository.BranchRepository;
import com.bagas.pinjam100.repository.userrolepermission.RoleRepository;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceTest")
class UserServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ROLE_ID = UUID.randomUUID();
    private static final UUID BRANCH_ID = UUID.randomUUID();
    private static final String IDENTITY_NUMBER = "1234567890";
    private static final String NEW_IDENTITY_NUMBER = "0987654321";
    private static final String NAME = "Bagas Aditya";
    private static final String RAW_PASSWORD = "Password123!";
    private static final String ENCODED_PASSWORD = "encoded_password_123";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("findAllByDeletedDateIsNull")
    class FindAllByDeletedDateIsNullTest {

        @Test
        @DisplayName("should return list of user responses when active users exist")
        void shouldReturnListOfUserResponses() {
            User user1 = createUser(USER_ID, IDENTITY_NUMBER, NAME);
            User user2 = createUser(UUID.randomUUID(), NEW_IDENTITY_NUMBER, "Customer Service");

            when(userRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of(user1, user2));

            List<UserResponse> result = userService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(NAME, result.get(0).getName());
            assertEquals("Customer Service", result.get(1).getName());

            verify(userRepository).findAllByDeletedDateIsNull();
        }

        @Test
        @DisplayName("should return empty list when no active users exist")
        void shouldReturnEmptyListWhenNoUsersExist() {
            when(userRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of());

            List<UserResponse> result = userService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(userRepository).findAllByDeletedDateIsNull();
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedDateIsNull")
    class FindByIdAndDeletedDateIsNullTest {

        @Test
        @DisplayName("should return user response when user found by id")
        void shouldReturnUserResponseWhenFound() {
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));

            UserResponse result = userService.findByIdAndDeletedDateIsNull(USER_ID);

            assertNotNull(result);
            assertEquals(USER_ID, result.getId());
            assertEquals(NAME, result.getName());

            verify(userRepository).findByIdAndDeletedDateIsNull(USER_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user not found by id")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.findByIdAndDeletedDateIsNull(USER_ID)
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
            verify(userRepository).findByIdAndDeletedDateIsNull(USER_ID);
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("should create user successfully when request is valid")
        void shouldSaveUserSuccessfully() {
            UserRequest request = createUserRequest(IDENTITY_NUMBER, NAME, ROLE_ID.toString(), BRANCH_ID.toString());
            Role role = createRole(ROLE_ID);
            Branch branch = createBranch(BRANCH_ID);

            when(userRepository.existsByIdentityNumber(request.getIdentityNumber()))
                    .thenReturn(false);
            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn(ENCODED_PASSWORD);
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.of(branch));
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> {
                        User savedUser = invocation.getArgument(0);
                        savedUser.setId(USER_ID);
                        return savedUser;
                    });

            UserResponse result = userService.save(request);

            assertNotNull(result);
            assertEquals(USER_ID, result.getId());
            assertEquals(NAME, result.getName());

            verify(userRepository).existsByIdentityNumber(request.getIdentityNumber());
            verify(passwordEncoder).encode(request.getPassword());
            verify(roleRepository).findByIdAndDeletedDateIsNull(ROLE_ID);
            verify(branchRepository).findByIdAndDeletedDateIsNull(BRANCH_ID);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ConflictException when identity number already exists")
        void shouldThrowExceptionWhenIdentityNumberExists() {
            UserRequest request = createUserRequest(IDENTITY_NUMBER, NAME, ROLE_ID.toString(), BRANCH_ID.toString());

            when(userRepository.existsByIdentityNumber(request.getIdentityNumber()))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> userService.save(request)
            );

            assertEquals("Nomor identitas sudah terdaftar", exception.getMessage());

            verify(userRepository).existsByIdentityNumber(request.getIdentityNumber());
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when role is not found")
        void shouldThrowExceptionWhenRoleNotFound() {
            UserRequest request = createUserRequest(IDENTITY_NUMBER, NAME, ROLE_ID.toString(), BRANCH_ID.toString());

            when(userRepository.existsByIdentityNumber(request.getIdentityNumber()))
                    .thenReturn(false);
            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn(ENCODED_PASSWORD);
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.save(request)
            );

            assertEquals("Role tidak ditemukan", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when branch is not found")
        void shouldThrowExceptionWhenBranchNotFound() {
            UserRequest request = createUserRequest(IDENTITY_NUMBER, NAME, ROLE_ID.toString(), BRANCH_ID.toString());
            Role role = createRole(ROLE_ID);

            when(userRepository.existsByIdentityNumber(request.getIdentityNumber()))
                    .thenReturn(false);
            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn(ENCODED_PASSWORD);
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.save(request)
            );

            assertEquals("Cabang tidak ditemukan", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update user successfully without identity number conflict check when unchanged")
        void shouldUpdateUserSuccessfullyWhenIdentityUnchanged() {
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);
            UserRequest request = createUserRequest(IDENTITY_NUMBER, "Bagas Updated", ROLE_ID.toString(), BRANCH_ID.toString());
            Role role = createRole(ROLE_ID);
            Branch branch = createBranch(BRANCH_ID);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn(ENCODED_PASSWORD);
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.of(branch));
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.update(USER_ID, request);

            assertNotNull(result);
            assertEquals("Bagas Updated", result.getName());

            verify(userRepository, never()).existsByIdentityNumber(any());
            verify(branchRepository).findByIdAndDeletedDateIsNull(BRANCH_ID);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should update user successfully when identity number changed and unique")
        void shouldUpdateUserSuccessfullyWhenIdentityChanged() {
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);
            UserRequest request = createUserRequest(NEW_IDENTITY_NUMBER, "Bagas Updated", ROLE_ID.toString(), BRANCH_ID.toString());
            Role role = createRole(ROLE_ID);
            Branch branch = createBranch(BRANCH_ID);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(userRepository.existsByIdentityNumber(NEW_IDENTITY_NUMBER))
                    .thenReturn(false);
            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn(ENCODED_PASSWORD);
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.of(branch));
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.update(USER_ID, request);

            assertNotNull(result);
            assertEquals(NEW_IDENTITY_NUMBER, user.getIdentityNumber());

            verify(userRepository).existsByIdentityNumber(NEW_IDENTITY_NUMBER);
            verify(branchRepository).findByIdAndDeletedDateIsNull(BRANCH_ID);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw ConflictException when changed identity number already exists")
        void shouldThrowExceptionWhenNewIdentityNumberExists() {
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);
            UserRequest request = createUserRequest(NEW_IDENTITY_NUMBER, "Bagas Updated", ROLE_ID.toString(), BRANCH_ID.toString());

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(userRepository.existsByIdentityNumber(NEW_IDENTITY_NUMBER))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> userService.update(USER_ID, request)
            );

            assertEquals("Nomor identitas sudah terdaftar", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user to update is not found")
        void shouldThrowExceptionWhenUserToUpdateNotFound() {
            UserRequest request = createUserRequest(IDENTITY_NUMBER, NAME, ROLE_ID.toString(), BRANCH_ID.toString());

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.update(USER_ID, request)
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when branch to update is not found")
        void shouldThrowExceptionWhenBranchToUpdateNotFound() {
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);
            UserRequest request = createUserRequest(IDENTITY_NUMBER, "Bagas Updated", ROLE_ID.toString(), BRANCH_ID.toString());
            Role role = createRole(ROLE_ID);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn(ENCODED_PASSWORD);
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(branchRepository.findByIdAndDeletedDateIsNull(BRANCH_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.update(USER_ID, request)
            );

            assertEquals("Cabang tidak ditemukan", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateActive")
    class UpdateActiveTest {

        @Test
        @DisplayName("should toggle user status from true to false successfully")
        void shouldToggleStatusFromTrueToFalse() {
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);
            user.setStatus(true);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.updateActive(USER_ID);

            assertNotNull(result);
            assertFalse(user.getStatus());

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user for status update not found")
        void shouldThrowExceptionWhenUserNotFoundOnUpdateActive() {
            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.updateActive(USER_ID)
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("changeRole")
    class ChangeRoleTest {

        @Test
        @DisplayName("should change user role successfully")
        void shouldChangeRoleSuccessfully() {
            UUID newRoleId = UUID.randomUUID();
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);
            Role newRole = createRole(newRoleId);
            newRole.setRoleName("SUPER_ADMIN");

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(roleRepository.findByIdAndDeletedDateIsNull(newRoleId))
                    .thenReturn(Optional.of(newRole));
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.changeRole(USER_ID, newRoleId);

            assertNotNull(result);
            assertEquals(newRole, user.getRole());

            verify(roleRepository).findByIdAndDeletedDateIsNull(newRoleId);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when new role is not found")
        void shouldThrowExceptionWhenNewRoleNotFound() {
            UUID newRoleId = UUID.randomUUID();
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(roleRepository.findByIdAndDeletedDateIsNull(newRoleId))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.changeRole(USER_ID, newRoleId)
            );

            assertEquals("Role tidak ditemukan", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should soft delete user by setting deletedDate")
        void shouldSoftDeleteUserSuccessfully() {
            User user = createUser(USER_ID, IDENTITY_NUMBER, NAME);

            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserResponse result = userService.delete(USER_ID);

            assertNotNull(result);
            assertNotNull(user.getDeletedDate());

            verify(userRepository).findByIdAndDeletedDateIsNull(USER_ID);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user to delete is not found")
        void shouldThrowExceptionWhenUserToDeleteNotFound() {
            when(userRepository.findByIdAndDeletedDateIsNull(USER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.delete(USER_ID)
            );

            assertEquals("User tidak ditemukan", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    private User createUser(UUID id, String identityNumber, String name) {
        User user = new User();
        user.setId(id);
        user.setIdentityNumber(identityNumber);
        user.setName(name);
        user.setPassword(ENCODED_PASSWORD);
        user.setStatus(true);
        user.setRole(createRole(ROLE_ID));
        user.setBranch(createBranch(BRANCH_ID));
        return user;
    }

    private Role createRole(UUID roleId) {
        Role role = new Role();
        role.setId(roleId);
        role.setRoleName("ADMIN");
        return role;
    }

    private Branch createBranch(UUID branchId) {
        Branch branch = new Branch();
        branch.setId(branchId);
        branch.setName("Pusat");
        return branch;
    }

    private UserRequest createUserRequest(String identityNumber, String name, String roleId, String branchId) {
        UserRequest request = new UserRequest();
        request.setIdentityNumber(identityNumber);
        request.setName(name);
        request.setPassword(RAW_PASSWORD);
        request.setStatus(true);
        request.setRole(roleId);
        request.setBranch(branchId);
        return request;
    }
}