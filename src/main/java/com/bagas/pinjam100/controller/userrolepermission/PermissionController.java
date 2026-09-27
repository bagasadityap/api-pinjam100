package com.bagas.pinjam100.controller.userrolepermission;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.userrolepermission.PermissionRequest;
import com.bagas.pinjam100.dto.response.userrolepermission.PermissionResponse;
import com.bagas.pinjam100.service.userrolepermission.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permission")
@Tag(
        name = "Permissions",
        description = "Permission management operations"
)
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(
            summary = "Get all permissions",
            description = "Retrieve all permissions that have not been deleted"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission data retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping
    public ResponseEntity<BaseResponse<List<PermissionResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data permission berhasil diambil",
                        permissionService.findAllByDeletedDateIsNull()
                )
        );
    }

    @Operation(
            summary = "Get permission by ID",
            description = "Retrieve a permission by its ID if the permission has not been deleted"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission data retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Permission not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Permission tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PermissionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data permission berhasil diambil",
                        permissionService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @Operation(
            summary = "Create permission",
            description = "Create a new permission"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Permission created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid permission data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data request tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Permission already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Permission sudah terdaftar\",\"error\":\"Conflict\",\"status\":409,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('permission:write')")
    @PostMapping
    public ResponseEntity<BaseResponse<PermissionResponse>> create(
            @RequestBody PermissionRequest permissionRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(
                        HttpStatus.CREATED,
                        "Permission berhasil dibuat",
                        permissionService.save(permissionRequest)
                )
        );
    }

    @Operation(
            summary = "Update permission",
            description = "Update an existing permission's information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid permission data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data request tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Permission not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Permission tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('permission:write')")
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

    @Operation(
            summary = "Delete permission",
            description = "Delete a permission"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Permission deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Permission not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Permission tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('permission:delete')")
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