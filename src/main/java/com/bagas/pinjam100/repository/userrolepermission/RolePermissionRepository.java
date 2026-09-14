package com.bagas.pinjam100.repository.userrolepermission;

import com.bagas.pinjam100.entity.userrolepermission.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolePermissionRepository  extends JpaRepository<RolePermission, UUID> {
    void findByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
    void deleteByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
    void deleteByRoleId(UUID roleId);
}
