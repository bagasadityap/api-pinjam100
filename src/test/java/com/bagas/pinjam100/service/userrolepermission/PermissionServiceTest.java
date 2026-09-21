package com.bagas.pinjam100.service.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.PermissionRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.PermissionResponse;
import com.bagas.pinjam100.entity.userrolepermission.Permission;
import com.bagas.pinjam100.exception.ConflictException;
import com.bagas.pinjam100.repository.userrolepermission.PermissionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionServiceTest")
class PermissionServiceTest {

    private static final UUID PERMISSION_ID = UUID.randomUUID();
    private static final String PERMISSION_NAME = "READ_DATA";
    private static final String NEW_PERMISSION_NAME = "WRITE_DATA";

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Nested
    @DisplayName("findAllByDeletedDateIsNull")
    class FindAllByDeletedDateIsNullTest {

        @Test
        @DisplayName("should return list of permission responses when active permissions exist")
        void shouldReturnListOfPermissionResponses() {
            Permission permission1 = createPermission(PERMISSION_ID, PERMISSION_NAME);
            Permission permission2 = createPermission(UUID.randomUUID(), NEW_PERMISSION_NAME);

            when(permissionRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of(permission1, permission2));

            List<PermissionResponse> result = permissionService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(PERMISSION_NAME, result.get(0).getPermissionName());
            assertEquals(NEW_PERMISSION_NAME, result.get(1).getPermissionName());

            verify(permissionRepository).findAllByDeletedDateIsNull();
        }

        @Test
        @DisplayName("should return empty list when no active permissions exist")
        void shouldReturnEmptyListWhenNoPermissionsExist() {
            when(permissionRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of());

            List<PermissionResponse> result = permissionService.findAllByDeletedDateIsNull();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(permissionRepository).findAllByDeletedDateIsNull();
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedDateIsNull")
    class FindByIdAndDeletedDateIsNullTest {

        @Test
        @DisplayName("should return permission response when permission found by id")
        void shouldReturnPermissionResponseWhenFound() {
            Permission permission = createPermission(PERMISSION_ID, PERMISSION_NAME);

            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.of(permission));

            PermissionResponse result = permissionService.findByIdAndDeletedDateIsNull(PERMISSION_ID);

            assertNotNull(result);
            assertEquals(PERMISSION_ID, result.getId());
            assertEquals(PERMISSION_NAME, result.getPermissionName());

            verify(permissionRepository).findByIdAndDeletedDateIsNull(PERMISSION_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when permission not found by id")
        void shouldThrowExceptionWhenPermissionNotFound() {
            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> permissionService.findByIdAndDeletedDateIsNull(PERMISSION_ID)
            );

            assertEquals("Permission tidak ditemukan", exception.getMessage());
            verify(permissionRepository).findByIdAndDeletedDateIsNull(PERMISSION_ID);
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("should create permission successfully when permission name is unique")
        void shouldSavePermissionSuccessfully() {
            PermissionRequest request = createPermissionRequest(PERMISSION_NAME);

            when(permissionRepository.existsByPermissionNameAndDeletedDateIsNull(request.getPermissionName()))
                    .thenReturn(false);
            when(permissionRepository.save(any(Permission.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PermissionResponse result = permissionService.save(request);

            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(PERMISSION_NAME, result.getPermissionName());

            verify(permissionRepository).existsByPermissionNameAndDeletedDateIsNull(request.getPermissionName());
            verify(permissionRepository).save(any(Permission.class));
        }

        @Test
        @DisplayName("should throw ConflictException when permission name already exists")
        void shouldThrowExceptionWhenPermissionNameExists() {
            PermissionRequest request = createPermissionRequest(PERMISSION_NAME);

            when(permissionRepository.existsByPermissionNameAndDeletedDateIsNull(request.getPermissionName()))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> permissionService.save(request)
            );

            assertEquals("Permission sudah ada", exception.getMessage());
            verify(permissionRepository).existsByPermissionNameAndDeletedDateIsNull(request.getPermissionName());
            verify(permissionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("should update permission successfully when name is unchanged (case-insensitive)")
        void shouldUpdatePermissionSuccessfullyWhenNameUnchanged() {
            Permission permission = createPermission(PERMISSION_ID, PERMISSION_NAME);
            PermissionRequest request = createPermissionRequest("read_data");

            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.of(permission));
            when(permissionRepository.save(any(Permission.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PermissionResponse result = permissionService.update(PERMISSION_ID, request);

            assertNotNull(result);
            assertEquals("read_data", permission.getPermissionName());

            verify(permissionRepository, never()).existsByPermissionNameAndDeletedDateIsNull(any());
            verify(permissionRepository).save(permission);
        }

        @Test
        @DisplayName("should update permission successfully when name is changed and unique")
        void shouldUpdatePermissionSuccessfullyWhenNameChanged() {
            Permission permission = createPermission(PERMISSION_ID, PERMISSION_NAME);
            PermissionRequest request = createPermissionRequest(NEW_PERMISSION_NAME);

            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.of(permission));
            when(permissionRepository.existsByPermissionNameAndDeletedDateIsNull(NEW_PERMISSION_NAME))
                    .thenReturn(false);
            when(permissionRepository.save(any(Permission.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PermissionResponse result = permissionService.update(PERMISSION_ID, request);

            assertNotNull(result);
            assertEquals(NEW_PERMISSION_NAME, permission.getPermissionName());

            verify(permissionRepository).existsByPermissionNameAndDeletedDateIsNull(NEW_PERMISSION_NAME);
            verify(permissionRepository).save(permission);
        }

        @Test
        @DisplayName("should throw ConflictException when new permission name already exists")
        void shouldThrowExceptionWhenNewPermissionNameExists() {
            Permission permission = createPermission(PERMISSION_ID, PERMISSION_NAME);
            PermissionRequest request = createPermissionRequest(NEW_PERMISSION_NAME);

            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.of(permission));
            when(permissionRepository.existsByPermissionNameAndDeletedDateIsNull(NEW_PERMISSION_NAME))
                    .thenReturn(true);

            ConflictException exception = assertThrows(
                    ConflictException.class,
                    () -> permissionService.update(PERMISSION_ID, request)
            );

            assertEquals("Permission sudah ada", exception.getMessage());
            verify(permissionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when permission to update is not found")
        void shouldThrowExceptionWhenPermissionToUpdateNotFound() {
            PermissionRequest request = createPermissionRequest(NEW_PERMISSION_NAME);

            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> permissionService.update(PERMISSION_ID, request)
            );

            assertEquals("Permission tidak ditemukan", exception.getMessage());
            verify(permissionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should soft delete permission by setting deletedDate")
        void shouldSoftDeletePermissionSuccessfully() {
            Permission permission = createPermission(PERMISSION_ID, PERMISSION_NAME);

            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.of(permission));
            when(permissionRepository.save(any(Permission.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PermissionResponse result = permissionService.delete(PERMISSION_ID);

            assertNotNull(result);
            assertNotNull(permission.getDeletedDate());

            verify(permissionRepository).findByIdAndDeletedDateIsNull(PERMISSION_ID);
            verify(permissionRepository).save(permission);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when permission to delete is not found")
        void shouldThrowExceptionWhenPermissionToDeleteNotFound() {
            when(permissionRepository.findByIdAndDeletedDateIsNull(PERMISSION_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> permissionService.delete(PERMISSION_ID)
            );

            assertEquals("Permission tidak ditemukan", exception.getMessage());
            verify(permissionRepository, never()).save(any());
        }
    }

    private Permission createPermission(UUID id, String permissionName) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setPermissionName(permissionName);
        return permission;
    }

    private PermissionRequest createPermissionRequest(String permissionName) {
        PermissionRequest request = new PermissionRequest();
        request.setPermissionName(permissionName);
        return request;
    }
}
