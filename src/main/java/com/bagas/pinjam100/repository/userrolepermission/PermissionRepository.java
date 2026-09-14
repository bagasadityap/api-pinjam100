package com.bagas.pinjam100.repository.userrolepermission;

import com.bagas.pinjam100.entity.userrolepermission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByIdAndDeletedDateIsNull(UUID id);
    List<Permission> findAllByDeletedDateIsNull();
}
