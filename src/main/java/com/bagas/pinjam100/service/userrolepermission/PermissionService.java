package com.bagas.pinjam100.service.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.PermissionRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.PermissionResponse;
import com.bagas.pinjam100.entity.userrolepermission.Permission;
import com.bagas.pinjam100.repository.userrolepermission.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll()
                .stream()
                .map(PermissionResponse::new)
                .toList();
    }

    public List<PermissionResponse> findAllByDeletedDateIsNull() {
        return permissionRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(PermissionResponse::new)
                .toList();
    }

    public PermissionResponse findById(UUID id) {
        Permission response = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission tidak ditemukan"));
        return new PermissionResponse(response);
    }

    public PermissionResponse findByIdAndDeletedDateIsNull(UUID id) {
        Permission response = permissionRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission tidak ditemukan"));
        return new PermissionResponse(response);
    }

    public PermissionResponse save(PermissionRequest request) {
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setPermissionName(request.getPermissionName());
        permissionRepository.save(permission);

        return new PermissionResponse(permission);
    }

    public PermissionResponse update(UUID id, PermissionRequest request) {
        Permission permission = permissionRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission tidak ditemukan"));

        permission.setPermissionName(request.getPermissionName());
        permissionRepository.save(permission);

        return new PermissionResponse(permission);
    }

    public PermissionResponse delete(UUID id) {
        Permission permission = permissionRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission tidak ditemukan"));

        permission.setDeletedDate(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
        permissionRepository.save(permission);
        return new PermissionResponse(permission);
    }
}
