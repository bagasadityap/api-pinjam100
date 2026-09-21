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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RoleService {

    public static final String CACHE_ROLE = "role";
    public static final String CACHE_ROLE_ALL = "role_all";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Cacheable(cacheNames = CACHE_ROLE_ALL, key = "'all_active'")
    public List<RoleResponse> findAllByDeletedDateIsNull() {
        return roleRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(RoleResponse::new)
                .toList();
    }

    @Cacheable(cacheNames = CACHE_ROLE, key = "#id")
    public RoleResponse findByIdAndDeletedDateIsNull(UUID id) {
        Role response = roleRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));
        return new RoleResponse(response);
    }

    @CacheEvict(cacheNames = CACHE_ROLE_ALL, key = "'all_active'")
    public RoleResponse save(RoleRequest request) {
        if (roleRepository.existsByRoleNameAndDeletedDateIsNull(request.getRoleName())) {
            throw new ConflictException("Nama role sudah ada");
        }
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        roleRepository.save(role);

        return new RoleResponse(role);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_ROLE, key = "#id"),
            @CacheEvict(cacheNames = CACHE_ROLE_ALL, key = "'all_active'")
    })
    public RoleResponse update(UUID id, RoleRequest request) {
        Role role = roleRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));

        // Validasi agar tidak melempar ConflictException jika nama role tidak diubah
        if (!role.getRoleName().equalsIgnoreCase(request.getRoleName()) &&
                roleRepository.existsByRoleNameAndDeletedDateIsNull(request.getRoleName())) {
            throw new ConflictException("Nama role sudah ada");
        }

        role.setRoleName(request.getRoleName());
        roleRepository.save(role);

        return new RoleResponse(role);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_ROLE, key = "#id"),
            @CacheEvict(cacheNames = CACHE_ROLE_ALL, key = "'all_active'")
    })
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

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_ROLE, key = "#id"),
            @CacheEvict(cacheNames = CACHE_ROLE_ALL, key = "'all_active'")
    })
    public RoleResponse delete(UUID id) {
        Role role = roleRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Role tidak ditemukan"));

        role.setDeletedDate(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
        roleRepository.save(role);
        return new RoleResponse(role);
    }
}