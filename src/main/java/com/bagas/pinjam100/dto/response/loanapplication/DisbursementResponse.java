package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.entity.customer.Rekening;
import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisbursementResponse {
    private UUID id;
    private UUID loanApplicationId;
    private String transactionReference;
    private BigDecimal disbursementAmount;
    private LocalDateTime createdDate;
    private Rekening rekening;

    public DisbursementResponse(LoanDisbursement disbursement) {
        this.id = disbursement.getId();
        this.loanApplicationId = disbursement.getLoanApplication().getId();
        this.transactionReference = disbursement.getTransactionReference();
        this.disbursementAmount = disbursement.getDisbursementAmount();
        this.createdDate = disbursement.getCreatedDate();
        this.rekening = disbursement.getRekening();
    }
}
