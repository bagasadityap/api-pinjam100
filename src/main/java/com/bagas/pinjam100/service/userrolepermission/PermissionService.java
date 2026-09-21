package com.bagas.pinjam100.service.userrolepermission;

import com.bagas.pinjam100.config.CacheNames;
import com.bagas.pinjam100.dto.request.userrolepermission.PermissionRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.PermissionResponse;
import com.bagas.pinjam100.entity.userrolepermission.Permission;
import com.bagas.pinjam100.exception.ConflictException;
import com.bagas.pinjam100.repository.userrolepermission.PermissionRepository;
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
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Cacheable(cacheNames = CacheNames.CACHE_PERMISSION_ALL, key = "'all_active'")
    public List<PermissionResponse> findAllByDeletedDateIsNull() {
        return permissionRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(PermissionResponse::new)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.CACHE_PERMISSION, key = "#id")
    public PermissionResponse findByIdAndDeletedDateIsNull(UUID id) {
        Permission response = permissionRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission tidak ditemukan"));
        return new PermissionResponse(response);
    }

    @CacheEvict(cacheNames = CacheNames.CACHE_PERMISSION_ALL, key = "'all_active'")
    public PermissionResponse save(PermissionRequest request) {
        if (permissionRepository.existsByPermissionNameAndDeletedDateIsNull(request.getPermissionName())) {
            throw new ConflictException("Permission sudah ada");
        }

        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setPermissionName(request.getPermissionName());
        permissionRepository.save(permission);

        return new PermissionResponse(permission);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_PERMISSION, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_PERMISSION_ALL, key = "'all_active'")
    })
    public PermissionResponse update(UUID id, PermissionRequest request) {
        Permission permission = permissionRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission tidak ditemukan"));

        if (!permission.getPermissionName().equalsIgnoreCase(request.getPermissionName()) &&
                permissionRepository.existsByPermissionNameAndDeletedDateIsNull(request.getPermissionName())) {
            throw new ConflictException("Permission sudah ada");
        }

        permission.setPermissionName(request.getPermissionName());
        permissionRepository.save(permission);

        return new PermissionResponse(permission);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_PERMISSION, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_PERMISSION_ALL, key = "'all_active'")
    })
    public PermissionResponse delete(UUID id) {
        Permission permission = permissionRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission tidak ditemukan"));

        permission.setDeletedDate(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
        permissionRepository.save(permission);
        return new PermissionResponse(permission);
    }
}