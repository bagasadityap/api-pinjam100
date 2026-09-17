package com.bagas.pinjam100.dto.request.loanapplication;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class LoanApplicationRequest {
    private UUID customerId;
    private BigDecimal loanAmount;
    private Integer tenorMonths;
    private String purpose;
}
