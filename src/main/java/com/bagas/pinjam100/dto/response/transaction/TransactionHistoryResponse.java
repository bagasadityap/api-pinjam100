package com.bagas.pinjam100.dto.response.transaction;

import com.bagas.pinjam100.service.transaction.TransactionStatus;
import com.bagas.pinjam100.service.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransactionHistoryResponse {

    private UUID id;
    private TransactionType type;
    private String referenceNumber;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionStatus status;
}
