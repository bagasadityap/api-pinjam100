package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.request.userrolepermission.PermissionRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.PermissionResponse;
import com.bagas.pinjam100.service.userrolepermission.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<PermissionResponse> getAll() {
        return permissionService.findAllByDeletedDateIsNull();
    }

    @GetMapping("/{id}")
    public PermissionResponse getById(@PathVariable UUID id) {
        return permissionService.findByIdAndDeletedDateIsNull(id);
    }

    @PostMapping
    public PermissionResponse create(@RequestBody PermissionRequest permissionRequest) {
        return permissionService.save(permissionRequest);
    }

    @PutMapping("/{id}")
    public PermissionResponse update(@PathVariable UUID id, @RequestBody PermissionRequest permissionRequest) {
        return permissionService.update(id, permissionRequest);
    }

    @DeleteMapping("/{id}")
    public PermissionResponse delete(@PathVariable UUID id) {
        return permissionService.delete(id);
    }
}
