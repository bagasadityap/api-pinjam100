package com.bagas.pinjam100.service.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.RoleRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.RoleResponse;
import com.bagas.pinjam100.entity.userrolepermission.Permission;
import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.entity.userrolepermission.RolePermission;
import com.bagas.pinjam100.repository.userrolepermission.PermissionRepository;
import com.bagas.pinjam100.repository.userrolepermission.RolePermissionRepository;
import com.bagas.pinjam100.repository.userrolepermission.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public List<RoleResponse> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(RoleResponse::new)
                .toList();
    }

    public List<RoleResponse> findAllByDeletedDateIsNull() {
        return roleRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(RoleResponse::new)
                .toList();
    }

    public RoleResponse findById(UUID id) {
        Role response = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));
        return new RoleResponse(response);
    }

    public RoleResponse findByIdAndDeletedDateIsNull(UUID id) {
        Role response = roleRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));
        return new RoleResponse(response);
    }

    public RoleResponse save(RoleRequest request) {
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        roleRepository.save(role);

        return new RoleResponse(role);
    }

    public RoleResponse update(UUID id, RoleRequest request) {
        Role role = roleRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));

        role.setRoleName(request.getRoleName());
        roleRepository.save(role);

        return new RoleResponse(role);
    }

    @Transactional
    public RoleResponse updatePermission(UUID id, List<UUID> permissions) {
        Role role = roleRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));

        List<Permission> permissionList = permissionRepository.findAllById(permissions);

        if (permissionList.size() != permissions.size()) {
            throw new EntityNotFoundException("Permission tidak ditemukan");
        }

        rolePermissionRepository.deleteByRoleId(id);

        List<RolePermission> rolePermissions = permissionList.stream()
                .map(permission -> new RolePermission(role, permission))
                .toList();

        rolePermissionRepository.saveAll(rolePermissions);
        rolePermissionRepository.flush();
        return new RoleResponse(role);
    }

    public RoleResponse delete(UUID id) {
        Role role = roleRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));

        role.setDeletedDate(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
        roleRepository.save(role);
        return new RoleResponse(role);
    }
}
