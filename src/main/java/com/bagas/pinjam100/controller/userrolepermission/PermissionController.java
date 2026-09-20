package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.userrolepermission.PermissionRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.PermissionResponse;
import com.bagas.pinjam100.service.userrolepermission.PermissionService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BaseResponse<List<PermissionResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data permission berhasil diambil",
                        permissionService.findAllByDeletedDateIsNull()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PermissionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data permission berhasil diambil",
                        permissionService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PermissionResponse>> create(
            @RequestBody PermissionRequest permissionRequest
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Permission berhasil dibuat",
                        permissionService.save(permissionRequest)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PermissionResponse>> update(
            @PathVariable UUID id,
            @RequestBody PermissionRequest permissionRequest
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Permission berhasil diperbarui",
                        permissionService.update(id, permissionRequest)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<PermissionResponse>> delete(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Permission berhasil dihapus",
                        permissionService.delete(id)
                )
        );
    }
}