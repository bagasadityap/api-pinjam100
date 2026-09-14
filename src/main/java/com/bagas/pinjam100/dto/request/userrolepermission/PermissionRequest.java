package com.bagas.pinjam100.dto.request.userrolepermission;

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
public class PermissionRequest {
    private String permissionName;

    public PermissionRequest(Permission permission) {
        this.permissionName = permission.getPermissionName();
    }
}

