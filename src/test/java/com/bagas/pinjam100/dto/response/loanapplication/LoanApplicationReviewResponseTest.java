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

@DisplayName("LoanApplicationReviewResponseTest")
class LoanApplicationReviewResponseTest {

    @Test
    @DisplayName("should map LoanApplicationReviewResponse correctly with all fields and branch")
    void shouldMapLoanApplicationReviewResponseSuccessfully() {
        UUID id = UUID.randomUUID();
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID());
        branch.setCity("Jakarta Selatan");

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(id);
        loanApplication.setApplicationId("APP-20260101-002");
        loanApplication.setLoanAmount(BigDecimal.valueOf(3000000));
        loanApplication.setInstallmentAmount(BigDecimal.valueOf(550000));
        loanApplication.setTenorMonths(6);
        loanApplication.setInterestRate(BigDecimal.valueOf(2.5));
        loanApplication.setPurpose("Modal Kerja");
        loanApplication.setStatus(LoanApplicationStatus.UNDER_REVIEW);
        loanApplication.setBranch(branch);

        CustomerDetailResponse customer = new CustomerDetailResponse();

        LoanApplicationReviewResponse response = new LoanApplicationReviewResponse(
                loanApplication,
                customer
        );

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("APP-20260101-002", response.getApplicationId());
        assertEquals(BigDecimal.valueOf(3000000), response.getLoanAmount());
        assertEquals(BigDecimal.valueOf(550000), response.getInstallmentAmount());
        assertEquals(6, response.getTenorMonths());
        assertEquals(BigDecimal.valueOf(2.5), response.getInterestRate());
        assertEquals("Modal Kerja", response.getPurpose());
        assertEquals(LoanApplicationStatus.UNDER_REVIEW, response.getStatus());
        assertNotNull(response.getCustomer());
        assertNotNull(response.getBranch());
    }

    @Test
    @DisplayName("should handle null branch gracefully")
    void shouldHandleNullBranch() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setBranch(null);

        LoanApplicationReviewResponse response = new LoanApplicationReviewResponse(
                loanApplication,
                null
        );

        assertNotNull(response);
        assertNull(response.getBranch());
        assertNull(response.getCustomer());
    }

    @Test
    @DisplayName("should test no-args constructor and setters/getters")
    void shouldTestGettersAndSetters() {
        LoanApplicationReviewResponse response = new LoanApplicationReviewResponse();
        response.setId(UUID.randomUUID());
        response.setReview(new ReviewResponse());

        assertNotNull(response.getId());
        assertNotNull(response.getReview());
    }
}
