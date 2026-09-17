package com.bagas.pinjam100.controller.transaction;

import com.bagas.pinjam100.dto.response.transaction.TransactionHistoryResponse;
import com.bagas.pinjam100.service.transaction.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
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
    public List<TransactionHistoryResponse> getByCustomerId(
            @PathVariable UUID customerId
    ) {
        return transactionHistoryService.getByCustomerId(customerId);
    }
}
