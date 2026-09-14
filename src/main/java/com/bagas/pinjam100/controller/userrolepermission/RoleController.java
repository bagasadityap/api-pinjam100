package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.RolePermissionsRequest;
import com.bagas.pinjam100.dto.request.userrolepermission.RoleRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.RoleResponse;
import com.bagas.pinjam100.service.userrolepermission.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/role")
public class  RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.findAllByDeletedDateIsNull();
    }

    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleService.findByIdAndDeletedDateIsNull(id);
    }

    @PostMapping
    public RoleResponse create(@RequestBody RoleRequest roleRequest) {
        return roleService.save(roleRequest);
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable UUID id, @RequestBody RoleRequest roleRequest) {
        return roleService.update(id, roleRequest);
    }

    @PatchMapping("/{id}/permission")
    public RoleResponse updatePermission(
            @PathVariable UUID id,
            @RequestBody RolePermissionsRequest request
    ) {
        return roleService.updatePermission(id, request.getPermissions());
    }

    @DeleteMapping("/{id}")
    public RoleResponse delete(@PathVariable UUID id) {
        return roleService.delete(id);
    }
}
