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
public class DocumentCheckerDashboardResponse {
    private Integer totalCustomers;
    private Integer pendingVerification;
    private Integer verifiedCustomers;
    private Integer rejectedCustomers;
    private List<CustomerResponse> recentCustomers;
}