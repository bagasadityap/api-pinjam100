package com.bagas.pinjam100.dto.installment;

import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class InstallmentResponse {
    private UUID id;
    private String loanApplicationId;
    private String installmentNumber;
    private Integer installmentSequence;
    private LocalDate dueDate;
    private BigDecimal paidAmount;
    private InstallmentStatus status;
    private LocalDateTime paidDate;

    public InstallmentResponse(LoanInstallment loanInstallment) {
        this.id = loanInstallment.getId();
        this.loanApplicationId = loanInstallment.getLoanApplication().getApplicationId();
        this.installmentNumber = loanInstallment.getInstallmentNumber();
        this.installmentSequence = loanInstallment.getInstallmentSequence();
        this.dueDate = loanInstallment.getDueDate();
        this.paidAmount = loanInstallment.getPaidAmount();
        this.status = loanInstallment.getStatus();
        this.paidDate = loanInstallment.getPaidDate();
    }
}
