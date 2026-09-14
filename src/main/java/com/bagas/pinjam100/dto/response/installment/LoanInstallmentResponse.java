package com.bagas.pinjam100.dto.response.installment;

import com.bagas.pinjam100.entity.installment.InstallmentStatus;
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
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private InstallmentStatus status;
    private LocalDateTime paidDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}