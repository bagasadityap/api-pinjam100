package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.LimitRequest;
import com.bagas.pinjam100.dto.response.customer.LimitResponse;
import com.bagas.pinjam100.service.customer.CustomerLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customer-limit")
@RequiredArgsConstructor
@Tag(
        name = "Customer Limits",
        description = "Customer credit limit management operations"
)
@SecurityRequirement(name = "bearerAuth")
public class LimitController {

    private final CustomerLimitService customerLimitService;

    @Operation(
            summary = "Get limit by customer ID",
            description = "Retrieve credit limit details for a specific customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer limit data retrieved successfully"
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
                    description = "Customer limit not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data limit customer tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}/customer")
    public ResponseEntity<BaseResponse<LimitResponse>> findByCustomer_Id(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data limit customer berhasil diambil",
                        customerLimitService.findByCustomer_Id(id)
                )
        );
    }

    @Operation(
            summary = "Save customer limit",
            description = "Set or create credit limit for a customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer limit saved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid limit request data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data request limit tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
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
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Customer tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('limit:write')")
    @PostMapping
    public ResponseEntity<BaseResponse<LimitResponse>> save(@RequestBody LimitRequest limitRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(
                        HttpStatus.CREATED,
                        "Limit customer berhasil disimpan",
                        customerLimitService.save(limitRequest)
                )
        );
    }
}