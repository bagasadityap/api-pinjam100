package com.bagas.pinjam100.dto.response.installment;

import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallmentResponse {
    private UUID id;
    private UUID loanApplicationId;
    private String installmentNumber;
    private Integer installmentSequence;
    private LocalDate dueDate;
    private BigDecimal installmentAmount;
    private BigDecimal paidAmount;
    private InstallmentStatus status;
    private LocalDateTime paidDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public LoanInstallmentResponse(LoanInstallment installment) {
        this.id = installment.getId();
        this.loanApplicationId = installment.getLoanApplication().getId();
        this.installmentNumber = installment.getInstallmentNumber();
        this.installmentSequence = installment.getInstallmentSequence();
        this.dueDate = installment.getDueDate();
        this.installmentAmount = installment.getInstallmentAmount();
        this.paidAmount = installment.getPaidAmount();
        this.status = installment.getStatus();
        this.paidDate = installment.getPaidDate();
        this.createdDate = installment.getCreatedDate();
        this.updatedDate = installment.getUpdatedDate();
    }
}