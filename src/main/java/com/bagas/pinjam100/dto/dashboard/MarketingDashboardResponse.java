package com.bagas.pinjam100.dto.dashboard;

import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketingDashboardResponse {

    private Integer totalRequests;
    private Double totalRequestGrowthRate;
    private Integer pendingRequests;
    private Integer approvedRequests;
    private Integer rejectedRequests;
    private Double approvalRate;
    private Double totalDisbursements;
    private Double totalDisbursementGrowthRate;
    private Integer totalCustomers;
    private Double totalCustomerGrowthRate;
    private Integer totalLoanOverdue;
    private BigDecimal totalLoanAmountOverdue;
    private List<LoanApplicationResponse> recentRequests;
}