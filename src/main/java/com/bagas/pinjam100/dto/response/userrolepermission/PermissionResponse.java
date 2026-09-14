package com.bagas.pinjam100.dto.response.userrolepermission;

import com.bagas.pinjam100.entity.userrolepermission.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private UUID id;
    private String permissionName;

    public PermissionResponse(Permission permission) {
        this.id = permission.getId();
        this.permissionName = permission.getPermissionName();
    }
}
