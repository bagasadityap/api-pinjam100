package com.bagas.pinjam100.controller.loanapplication;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.response.loanapplication.DisbursementResponse;
import com.bagas.pinjam100.service.loanapplication.LoanDisbursementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/disbursement")
@RequiredArgsConstructor
@Tag(
        name = "Loan Disbursements",
        description = "Loan disbursement management operations"
)
@SecurityRequirement(name = "bearerAuth")
public class LoanDisbursementController {

    private final LoanDisbursementService loanDisbursementService;

    @Operation(
            summary = "Get disbursement by ID",
            description = "Retrieve disbursement details by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Disbursement data retrieved successfully"
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
                    description = "Disbursement not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data pencairan tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<DisbursementResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pencairan berhasil ditemukan",
                        loanDisbursementService.getById(id)
                )
        );
    }
}