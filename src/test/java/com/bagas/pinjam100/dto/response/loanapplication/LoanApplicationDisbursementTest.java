package com.bagas.pinjam100.dto.response.loanapplication;

import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.entity.customer.Rekening;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanApplicationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanApplicationDisbursementResponseTest")
class LoanApplicationDisbursementResponseTest {

    @Test
    @DisplayName("should map LoanApplicationDisbursementResponse correctly with all fields and branch")
    void shouldMapLoanApplicationDisbursementResponseSuccessfully() {
        UUID id = UUID.randomUUID();
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID());
        branch.setCity("Jakarta Selatan");

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFullName("Bagas");

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(id);
        loanApplication.setApplicationId("APP-20260101-003");
        loanApplication.setLoanAmount(BigDecimal.valueOf(10000000));
        loanApplication.setTenorMonths(12);
        loanApplication.setInterestRate(BigDecimal.valueOf(2.0));
        loanApplication.setPurpose("Modal Usaha");
        loanApplication.setStatus(LoanApplicationStatus.APPROVED);
        loanApplication.setCustomer(customer);
        loanApplication.setBranch(branch);

        CustomerLimit customerLimit = new CustomerLimit();
        customerLimit.setAvailableLimit(BigDecimal.valueOf(5000000));

        Rekening rekening = new Rekening();
        rekening.setId(UUID.randomUUID());
        rekening.setNoRekening("1234567890");

        LoanApplicationDisbursementResponse response = new LoanApplicationDisbursementResponse(
                loanApplication,
                customerLimit,
                rekening
        );

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("APP-20260101-003", response.getApplicationId());
        assertEquals(BigDecimal.valueOf(10000000), response.getLoanAmount());
        assertEquals(12, response.getTenorMonths());
        assertEquals(BigDecimal.valueOf(2.0), response.getInterestRate());
        assertEquals("Modal Usaha", response.getPurpose());
        assertEquals(LoanApplicationStatus.APPROVED, response.getStatus());
        assertNotNull(response.getCustomer());
        assertNotNull(response.getBranch());
        assertNotNull(response.getRekening());
    }

    @Test
    @DisplayName("should handle null branch gracefully")
    void shouldHandleNullBranch() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setCustomer(customer);
        loanApplication.setBranch(null);

        CustomerLimit customerLimit = new CustomerLimit();
        Rekening rekening = new Rekening();

        LoanApplicationDisbursementResponse response = new LoanApplicationDisbursementResponse(
                loanApplication,
                customerLimit,
                rekening
        );

        assertNotNull(response);
        assertNull(response.getBranch());
    }

    @Test
    @DisplayName("should test no-args constructor and setters/getters")
    void shouldTestGettersAndSetters() {
        LoanApplicationDisbursementResponse response = new LoanApplicationDisbursementResponse();
        response.setId(UUID.randomUUID());
        response.setApproval(new ApprovalResponse());
        response.setDisbursement(new DisbursementResponse());

        assertNotNull(response.getId());
        assertNotNull(response.getApproval());
        assertNotNull(response.getDisbursement());
    }
}
