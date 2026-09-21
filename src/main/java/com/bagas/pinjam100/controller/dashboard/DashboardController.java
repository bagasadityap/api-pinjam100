package com.bagas.pinjam100.controller.dashboard;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
@Tag(
        name = "Dashboard",
        description = "Dashboard analytics and overview operations"
)
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Get dashboard data",
            description = "Retrieve summary metrics and analytical data for the dashboard"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard data retrieved successfully"
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
    @PreAuthorize("hasAuthority('dashboard:read')")
    @GetMapping
    public ResponseEntity<BaseResponse<?>> dashboard() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data dashboard berhasil ditemukan",
                        dashboardService.dashboard()
                )
        );
    }
}