package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationResponse {

    private UUID id;
    private String applicationId;
    private CustomerResponse customer;
    private BranchResponse branch;
    private BigDecimal loanAmount;
    private Integer tenorMonths;
    private BigDecimal interestRate;
    private String purpose;
    private LoanApplicationStatus status;
    private LocalDateTime createdDate;

    public LoanApplicationResponse(
            LoanApplication loanApplication,
            CustomerLimit customerLimit
    ) {
        this.id = loanApplication.getId();
        this.applicationId = loanApplication.getApplicationId();
        this.loanAmount = loanApplication.getLoanAmount();
        this.tenorMonths = loanApplication.getTenorMonths();
        this.interestRate = loanApplication.getInterestRate();
        this.purpose = loanApplication.getPurpose();
        this.status = loanApplication.getStatus();
        this.createdDate = loanApplication.getCreatedDate();

        if (loanApplication.getCustomer() != null) {
            this.customer = new CustomerResponse(
                    loanApplication.getCustomer(),
                    customerLimit
            );
        }

        if (loanApplication.getBranch() != null) {
            this.branch = new BranchResponse(loanApplication.getBranch());
        }
    }
}