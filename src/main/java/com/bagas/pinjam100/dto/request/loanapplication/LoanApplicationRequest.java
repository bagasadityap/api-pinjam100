package com.bagas.pinjam100.dto.request.loanapplication;

import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.customer.Customer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class LoanApplicationRequest {
    private UUID id;
    private Customer customer;
    private Branch branch;
    private BigDecimal loanAmount;
    private Integer tenor_months;
    private BigDecimal interestRate;
    private String purpose;
}
