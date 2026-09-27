package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.BranchRequest;
import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.service.BranchService;
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
@RequestMapping("/branch")
@Tag(
        name = "Branches",
        description = "Branch management operations"
)
@SecurityRequirement(name = "bearerAuth")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @Operation(
            summary = "Get all branches",
            description = "Retrieve all branches that have not been deleted"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Branch data retrieved successfully"
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
    @PreAuthorize("hasAuthority('branch:read')")
    @GetMapping
    public ResponseEntity<BaseResponse<List<BranchResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data branch berhasil diambil",
                        branchService.findAllByDeletedDateIsNull()
                )
        );
    }

    @Operation(
            summary = "Get branch by ID",
            description = "Retrieve a branch by its ID if the branch has not been deleted"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Branch data retrieved successfully"
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
                    description = "Branch not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Branch tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('branch:read')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<BranchResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data branch berhasil diambil",
                        branchService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @Operation(
            summary = "Create branch",
            description = "Create a new branch"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Branch created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid branch data",
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
                    description = "Branch already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Branch sudah terdaftar\",\"error\":\"Conflict\",\"status\":409,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('branch:write')")
    @PostMapping
    public ResponseEntity<BaseResponse<BranchResponse>> create(@RequestBody BranchRequest branchRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(
                        HttpStatus.CREATED,
                        "Branch berhasil dibuat",
                        branchService.save(branchRequest)
                )
        );
    }

    @Operation(
            summary = "Update branch",
            description = "Update an existing branch's information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Branch updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid branch data",
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
                    description = "Branch not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Branch tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('branch:write')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<BranchResponse>> update(@PathVariable UUID id, @RequestBody BranchRequest branchRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Branch berhasil diperbarui",
                        branchService.update(id, branchRequest)
                )
        );
    }

    @Operation(
            summary = "Delete branch",
            description = "Delete a branch"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Branch deleted successfully"
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
                    description = "Branch not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Branch tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('branch:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<BranchResponse>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Branch berhasil dihapus",
                        branchService.delete(id)
                )
        );
    }
}