package com.bagas.pinjam100.dto.dashboard;

import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDashboardResponse {
    private Integer totalRequests;
    private Integer approvedRequests;
    private Integer pendingDisbursements;
    private Double totalApprovedAmount;
    private Double totalDisbursements;
    private Double totalDisbursementGrowthRate;
    private List<LoanApplicationResponse> recentRequests;
}
