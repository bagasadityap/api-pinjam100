package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.dto.response.customer.RekeningResponse;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.entity.customer.Rekening;
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
public class LoanApplicationDisbursementResponse {
    private UUID id;
    private String applicationId;
    private BigDecimal loanAmount;
    private Integer tenorMonths;
    private BigDecimal interestRate;
    private String purpose;
    private LoanApplicationStatus status;

    private CustomerResponse customer;
    private BranchResponse branch;

    private ApprovalResponse approval;
    private RekeningResponse rekening;

    private DisbursementResponse disbursement;

    public LoanApplicationDisbursementResponse(
            LoanApplication response,
            CustomerLimit customerLimit
    ) {
        this.id = response.getId();
        this.applicationId = response.getApplicationId();
        this.loanAmount = response.getLoanAmount();
        this.tenorMonths = response.getTenorMonths();
        this.interestRate = response.getInterestRate();
        this.purpose = response.getPurpose();
        this.status = response.getStatus();

        this.customer = new CustomerResponse(
                response.getCustomer(),
                customerLimit
        );

        this.rekening = new RekeningResponse(
                response.getRekening()
        );

        if (response.getBranch() != null) {
            this.branch = new BranchResponse(response.getBranch());
        }
    }
}