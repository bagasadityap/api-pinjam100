package com.bagas.pinjam100.dto.request.userrolepermission;

import com.bagas.pinjam100.entity.userrolepermission.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    private String roleName;

    public RoleRequest(Role role) {
        this.roleName = role.getRoleName();
    }
}