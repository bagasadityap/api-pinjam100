package com.bagas.pinjam100.controller.transaction;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.installment.InstallmentResponse;
import com.bagas.pinjam100.dto.response.loanapplication.DisbursementResponse;
import com.bagas.pinjam100.dto.response.transaction.TransactionHistoryResponse;
import com.bagas.pinjam100.service.transaction.TransactionHistoryService;
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
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

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