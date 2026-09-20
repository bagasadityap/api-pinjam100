package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.userrolepermission.RolePermissionsRequest;
import com.bagas.pinjam100.dto.request.userrolepermission.RoleRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.RoleResponse;
import com.bagas.pinjam100.service.userrolepermission.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<RoleResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data role berhasil diambil",
                        roleService.findAllByDeletedDateIsNull()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RoleResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data role berhasil diambil",
                        roleService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<RoleResponse>> create(@RequestBody RoleRequest roleRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Role berhasil dibuat",
                        roleService.save(roleRequest)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<RoleResponse>> update(@PathVariable UUID id, @RequestBody RoleRequest roleRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Role berhasil diperbarui",
                        roleService.update(id, roleRequest)
                )
        );
    }

    @PatchMapping("/{id}/permission")
    public ResponseEntity<BaseResponse<RoleResponse>> updatePermission(
            @PathVariable UUID id,
            @RequestBody RolePermissionsRequest request
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Permission role berhasil diperbarui",
                        roleService.updatePermission(id, request.getPermissions())
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<RoleResponse>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Role berhasil dihapus",
                        roleService.delete(id)
                )
        );
    }
}