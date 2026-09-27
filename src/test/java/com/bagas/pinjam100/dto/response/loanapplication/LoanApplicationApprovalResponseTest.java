package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanApplicationApprovalResponseTest")
class LoanApplicationApprovalResponseTest {

    @Test
    @DisplayName("should map LoanApplicationApprovalResponse correctly with all fields and branches")
    void shouldMapLoanApplicationApprovalResponseSuccessfully() {
        UUID id = UUID.randomUUID();
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID());
        branch.setCity("Jakarta Selatan");

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(id);
        loanApplication.setApplicationId("APP-20260101-001");
        loanApplication.setLoanAmount(BigDecimal.valueOf(5000000));
        loanApplication.setInstallmentAmount(BigDecimal.valueOf(900000));
        loanApplication.setTenorMonths(6);
        loanApplication.setInterestRate(BigDecimal.valueOf(2.5));
        loanApplication.setPurpose("Modal");
        loanApplication.setStatus(LoanApplicationStatus.PASS_REVIEW);
        loanApplication.setBranch(branch);

        CustomerDetailResponse customer = new CustomerDetailResponse();
        ReviewResponse review = new ReviewResponse();

        LoanApplicationApprovalResponse response = new LoanApplicationApprovalResponse(
                loanApplication,
                customer,
                review
        );

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("APP-20260101-001", response.getApplicationId());
        assertEquals(BigDecimal.valueOf(5000000), response.getLoanAmount());
        assertEquals(BigDecimal.valueOf(900000), response.getInstallmentAmount());
        assertEquals(6, response.getTenorMonths());
        assertEquals(BigDecimal.valueOf(2.5), response.getInterestRate());
        assertEquals("Modal", response.getPurpose());
        assertEquals(LoanApplicationStatus.PASS_REVIEW, response.getStatus());
        assertNotNull(response.getCustomer());
        assertNotNull(response.getBranch());
        assertNotNull(response.getReview());
        assertNull(response.getApproval());
    }

    @Test
    @DisplayName("should handle null branch and null review gracefully")
    void shouldHandleNullBranchAndReview() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setBranch(null);

        LoanApplicationApprovalResponse response = new LoanApplicationApprovalResponse(
                loanApplication,
                null,
                null
        );

        assertNotNull(response);
        assertNull(response.getBranch());
        assertNull(response.getReview());
    }

    @Test
    @DisplayName("should test no-args constructor and setters/getters")
    void shouldTestGettersAndSetters() {
        LoanApplicationApprovalResponse response = new LoanApplicationApprovalResponse();
        response.setId(UUID.randomUUID());
        response.setApproval(new ApprovalResponse());

        assertNotNull(response.getId());
        assertNotNull(response.getApproval());
    }
}
