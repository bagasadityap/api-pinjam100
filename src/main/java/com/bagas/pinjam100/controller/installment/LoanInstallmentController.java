package com.bagas.pinjam100.controller.installment;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.response.installment.LoanInstallmentResponse;
import com.bagas.pinjam100.service.installment.LoanInstallmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/installment")
@RequiredArgsConstructor
@Tag(
        name = "Loan Installments",
        description = "Loan installment management operations"
)
@SecurityRequirement(name = "bearerAuth")
public class LoanInstallmentController {

    private final LoanInstallmentService loanInstallmentService;

    @Operation(
            summary = "Get installment by ID",
            description = "Retrieve loan installment details by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Installment data retrieved successfully"
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
                    description = "Installment not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data angsuran tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LoanInstallmentResponse>> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data angsuran berhasil ditemukan",
                        loanInstallmentService.getById(id)
                )
        );
    }

    @Operation(
            summary = "Get installments by loan application ID",
            description = "Retrieve all installments associated with a specific loan application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Installments retrieved successfully"
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
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @GetMapping("/{loanApplicationId}/loan-application")
    public ResponseEntity<BaseResponse<List<LoanInstallmentResponse>>> getByLoanApplicationId(
            @PathVariable UUID loanApplicationId
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data angsuran berhasil ditemukan",
                        loanInstallmentService.getByLoanApplication_Id(loanApplicationId)
                )
        );
    }

    @Operation(
            summary = "Get installments by customer ID",
            description = "Retrieve all installments for a specific customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer installments retrieved successfully"
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
    @GetMapping("/{customerId}/customer")
    public ResponseEntity<BaseResponse<List<LoanInstallmentResponse>>> getByCustomerId(
            @PathVariable UUID customerId
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data angsuran berhasil ditemukan",
                        loanInstallmentService.getByCustomerId(customerId)
                )
        );
    }

    @Operation(
            summary = "Pay installment",
            description = "Process payment for a specific loan installment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Installment paid successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Installment already paid or invalid status",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Angsuran sudah dibayar atau tidak dapat diproses\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
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
                    description = "Installment not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data angsuran tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping("/{id}/pay")
    public ResponseEntity<BaseResponse<LoanInstallmentResponse>> pay(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Angsuran berhasil dibayar",
                        loanInstallmentService.pay(id)
                )
        );
    }
}