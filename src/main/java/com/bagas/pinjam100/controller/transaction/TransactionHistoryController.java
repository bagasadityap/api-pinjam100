package com.bagas.pinjam100.controller.transaction;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.response.transaction.TransactionHistoryResponse;
import com.bagas.pinjam100.service.transaction.TransactionHistoryService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transaction-history")
@RequiredArgsConstructor
@Tag(
        name = "Transaction History",
        description = "Transaction history and audit operations"
)
@SecurityRequirement(name = "bearerAuth")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @Operation(
            summary = "Get transaction history by customer ID",
            description = "Retrieve all transaction history records for a specific customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction history retrieved successfully"
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
    public ResponseEntity<BaseResponse<List<TransactionHistoryResponse>>> getByCustomerId(
            @PathVariable UUID customerId
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data riwayat transaksi berhasil ditemukan",
                        transactionHistoryService.getByCustomerId(customerId)
                )
        );
    }
}