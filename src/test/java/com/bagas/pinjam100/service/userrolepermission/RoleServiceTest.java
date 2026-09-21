package com.bagas.pinjam100.service.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.RoleRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.RoleResponse;
import com.bagas.pinjam100.entity.userrolepermission.Permission;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.entity.userrolepermission.RolePermission;
import com.bagas.pinjam100.exception.ConflictException;
import com.bagas.pinjam100.repository.userrolepermission.PermissionRepository;
import com.bagas.pinjam100.repository.userrolepermission.RolePermissionRepository;
import com.bagas.pinjam100.repository.userrolepermission.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleServiceTest")
class RoleServiceTest {

    private static final UUID ROLE_ID = UUID.randomUUID();
    private static final UUID PERMISSION_ID_1 = UUID.randomUUID();
    private static final UUID PERMISSION_ID_2 = UUID.randomUUID();
    private static final String ROLE_NAME = "ADMIN";
    private static final String NEW_ROLE_NAME = "SUPER_ADMIN";

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private RoleService roleService;

    @Nested
    @DisplayName("findAllByDeletedDateIsNull")
    class FindAllByDeletedDateIsNullTest {

        @Test
        @DisplayName("should return list of role responses when active roles exist")
        void shouldReturnListOfRoleResponses() {
            Role role1 = createRole(ROLE_ID, ROLE_NAME);
            Role role2 = createRole(UUID.randomUUID(), "STAFF");

            when(roleRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of(role1, role2));

            List<RoleResponse> result = roleService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(ROLE_NAME, result.get(0).getRoleName());
            assertEquals("STAFF", result.get(1).getRoleName());

            verify(roleRepository).findAllByDeletedDateIsNull();
        }

        @Test
        @DisplayName("should return empty list when no active roles exist")
        void shouldReturnEmptyListWhenNoRolesExist() {
            when(roleRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of());

            List<RoleResponse> result = roleService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(roleRepository).findAllByDeletedDateIsNull();
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedDateIsNull")
    class FindByIdAndDeletedDateIsNullTest {

        @Test
        @DisplayName("should return role response when role found by id")
        void shouldReturnRoleResponseWhenFound() {
            Role role = createRole(ROLE_ID, ROLE_NAME);

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));

            RoleResponse result = roleService.findByIdAndDeletedDateIsNull(ROLE_ID);

            assertNotNull(result);
            assertEquals(ROLE_ID, result.getId());
            assertEquals(ROLE_NAME, result.getRoleName());

            verify(roleRepository).findByIdAndDeletedDateIsNull(ROLE_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when role not found by id")
        void shouldThrowExceptionWhenRoleNotFound() {
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> roleService.findByIdAndDeletedDateIsNull(ROLE_ID)
            );

            assertEquals("Role tidak ditemukan", exception.getMessage());
            verify(roleRepository).findByIdAndDeletedDateIsNull(ROLE_ID);
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("should create role successfully when role name is unique")
        void shouldSaveRoleSuccessfully() {
            RoleRequest request = createRoleRequest(ROLE_NAME);

            when(roleRepository.existsByRoleNameAndDeletedDateIsNull(request.getRoleName()))
                    .thenReturn(false);
            when(roleRepository.save(any(Role.class)))
                    .thenAnswer(invocation -> {
                        Role savedRole = invocation.getArgument(0);
                        savedRole.setId(ROLE_ID);
                        return savedRole;
                    });

            RoleResponse result = roleService.save(request);

            assertNotNull(result);
            assertEquals(ROLE_ID, result.getId());
            assertEquals(ROLE_NAME, result.getRoleName());

            verify(roleRepository).existsByRoleNameAndDeletedDateIsNull(request.getRoleName());
            verify(roleRepository).save(any(Role.class));
        }

        @Test
        @DisplayName("should throw ConflictException when role name already exists")
        void shouldThrowExceptionWhenRoleNameExists() {
            RoleRequest request = createRoleRequest(ROLE_NAME);

            when(roleRepository.existsByRoleNameAndDeletedDateIsNull(request.getRoleName()))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> roleService.save(request)
            );

            assertEquals("Nama role sudah ada", exception.getMessage());
            verify(roleRepository).existsByRoleNameAndDeletedDateIsNull(request.getRoleName());
            verify(roleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update role successfully when name is unchanged (case-insensitive)")
        void shouldUpdateRoleSuccessfullyWhenNameUnchanged() {
            Role role = createRole(ROLE_ID, ROLE_NAME);
            RoleRequest request = createRoleRequest("admin");

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(roleRepository.save(any(Role.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            RoleResponse result = roleService.update(ROLE_ID, request);

            assertNotNull(result);
            assertEquals("admin", role.getRoleName());

            verify(roleRepository, never()).existsByRoleNameAndDeletedDateIsNull(any());
            verify(roleRepository).save(role);
        }

        @Test
        @DisplayName("should update role successfully when name is changed and unique")
        void shouldUpdateRoleSuccessfullyWhenNameChanged() {
            Role role = createRole(ROLE_ID, ROLE_NAME);
            RoleRequest request = createRoleRequest(NEW_ROLE_NAME);

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(roleRepository.existsByRoleNameAndDeletedDateIsNull(NEW_ROLE_NAME))
                    .thenReturn(false);
            when(roleRepository.save(any(Role.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            RoleResponse result = roleService.update(ROLE_ID, request);

            assertNotNull(result);
            assertEquals(NEW_ROLE_NAME, role.getRoleName());

            verify(roleRepository).existsByRoleNameAndDeletedDateIsNull(NEW_ROLE_NAME);
            verify(roleRepository).save(role);
        }

        @Test
        @DisplayName("should throw ConflictException when new role name already exists")
        void shouldThrowExceptionWhenNewRoleNameExists() {
            Role role = createRole(ROLE_ID, ROLE_NAME);
            RoleRequest request = createRoleRequest(NEW_ROLE_NAME);

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(roleRepository.existsByRoleNameAndDeletedDateIsNull(NEW_ROLE_NAME))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> roleService.update(ROLE_ID, request)
            );

            assertEquals("Nama role sudah ada", exception.getMessage());
            verify(roleRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when role to update is not found")
        void shouldThrowExceptionWhenRoleToUpdateNotFound() {
            RoleRequest request = createRoleRequest(NEW_ROLE_NAME);

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> roleService.update(ROLE_ID, request)
            );

            assertEquals("Role tidak ditemukan", exception.getMessage());
            verify(roleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updatePermission")
    class UpdatePermissionTest {

        @Test
        @DisplayName("should update permissions for role successfully")
        void shouldUpdatePermissionsSuccessfully() {
            Role role = createRole(ROLE_ID, ROLE_NAME);
            List<UUID> permissionIds = List.of(PERMISSION_ID_1, PERMISSION_ID_2);
            Permission p1 = createPermission(PERMISSION_ID_1, "READ_DATA");
            Permission p2 = createPermission(PERMISSION_ID_2, "WRITE_DATA");

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(permissionRepository.findAllById(permissionIds))
                    .thenReturn(List.of(p1, p2));

            RoleResponse result = roleService.updatePermission(ROLE_ID, permissionIds);

            assertNotNull(result);
            verify(rolePermissionRepository).deleteByRoleId(ROLE_ID);
            verify(rolePermissionRepository).saveAll(argThat(list -> {
                List<RolePermission> rpList = (List<RolePermission>) list;
                return rpList.size() == 2;
            }));
            verify(rolePermissionRepository).flush();
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when role is not found on permission update")
        void shouldThrowExceptionWhenRoleNotFoundOnPermissionUpdate() {
            List<UUID> permissionIds = List.of(PERMISSION_ID_1);

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> roleService.updatePermission(ROLE_ID, permissionIds)
            );

            assertEquals("Role tidak ditemukan", exception.getMessage());
            verify(permissionRepository, never()).findAllById(any());
            verify(rolePermissionRepository, never()).deleteByRoleId(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when one or more permissions are not found")
        void shouldThrowExceptionWhenPermissionNotFound() {
            Role role = createRole(ROLE_ID, ROLE_NAME);
            List<UUID> permissionIds = List.of(PERMISSION_ID_1, PERMISSION_ID_2);
            Permission p1 = createPermission(PERMISSION_ID_1, "READ_DATA");

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(permissionRepository.findAllById(permissionIds))
                    .thenReturn(List.of(p1));

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> roleService.updatePermission(ROLE_ID, permissionIds)
            );

            assertEquals("Permission tidak ditemukan", exception.getMessage());
            verify(rolePermissionRepository, never()).deleteByRoleId(any());
            verify(rolePermissionRepository, never()).saveAll(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should soft delete role by setting deletedDate")
        void shouldSoftDeleteRoleSuccessfully() {
            Role role = createRole(ROLE_ID, ROLE_NAME);

            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.of(role));
            when(roleRepository.save(any(Role.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            RoleResponse result = roleService.delete(ROLE_ID);

            assertNotNull(result);
            assertNotNull(role.getDeletedDate());

            verify(roleRepository).findByIdAndDeletedDateIsNull(ROLE_ID);
            verify(roleRepository).save(role);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when role to delete is not found")
        void shouldThrowExceptionWhenRoleToDeleteNotFound() {
            when(roleRepository.findByIdAndDeletedDateIsNull(ROLE_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> roleService.delete(ROLE_ID)
            );

            assertEquals("Role tidak ditemukan", exception.getMessage());
            verify(roleRepository, never()).save(any());
        }
    }

    private Role createRole(UUID id, String roleName) {
        Role role = new Role();
        role.setId(id);
        role.setRoleName(roleName);
        return role;
    }

    private Permission createPermission(UUID id, String permissionName) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setPermissionName(permissionName);
        return permission;
    }

    private RoleRequest createRoleRequest(String roleName) {
        RoleRequest request = new RoleRequest();
        request.setRoleName(roleName);
        return request;
    }
}
