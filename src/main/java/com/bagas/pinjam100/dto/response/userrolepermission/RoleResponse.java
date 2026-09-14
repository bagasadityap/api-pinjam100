package com.bagas.pinjam100.dto.response.userrolepermission;

import com.bagas.pinjam100.entity.userrolepermission.Role;
import com.bagas.pinjam100.entity.userrolepermission.RolePermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private UUID id;
    private String roleName;
    private List<PermissionResponse> permissions;

    public RoleResponse(Role role) {
        this.id = role.getId();
        this.roleName = role.getRoleName();

        this.permissions = role.getRolePermissions()
                .stream()
                .map(RolePermission::getPermission)
                .map(PermissionResponse::new)
                .toList();
    }
}
