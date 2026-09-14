package com.bagas.pinjam100.dto.request.userrolepermission;

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
public class RolePermissionsRequest {
    List<UUID> permissions;
}
