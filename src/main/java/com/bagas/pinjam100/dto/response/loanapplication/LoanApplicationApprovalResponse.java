package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationApprovalResponse {
    private UUID id;
    private String applicationId;
    private BigDecimal loanAmount;
    private BigDecimal installmentAmount;
    private Integer tenorMonths;
    private BigDecimal interestRate;
    private String purpose;
    private LoanApplicationStatus status;

    private CustomerDetailResponse customer;
    private BranchResponse branch;

    private ReviewResponse review;
    private ApprovalResponse approval;

    public LoanApplicationApprovalResponse(LoanApplication response, CustomerDetailResponse customer, ReviewResponse review) {
        this.id = response.getId();
        this.applicationId = response.getApplicationId();
        this.loanAmount = response.getLoanAmount();
        this.installmentAmount = response.getInstallmentAmount();
        this.tenorMonths = response.getTenorMonths();
        this.interestRate = response.getInterestRate();
        this.purpose = response.getPurpose();
        this.status = response.getStatus();

        this.customer = customer;

        if (response.getBranch() != null) {
            this.branch = new BranchResponse(response.getBranch());
        }

        if (review != null) {
            this.review = review;
        }
    }
}