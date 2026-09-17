package com.bagas.pinjam100.dto.dashboard;

import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditAnalystDashboardResponse {
    private Integer totalCustomers;
    private Integer verifiedCustomers;
    private Integer pendingLimitAnalysis;
    private Integer customersWithLimit;
    private List<CustomerResponse> recentCustomers;
}