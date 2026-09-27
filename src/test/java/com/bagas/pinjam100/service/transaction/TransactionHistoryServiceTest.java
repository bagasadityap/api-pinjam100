package com.bagas.pinjam100.service.transaction;

import com.bagas.pinjam100.dto.response.transaction.TransactionHistoryResponse;
import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import com.bagas.pinjam100.repository.installment.LoanInstallmentRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationDisbursementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionHistoryServiceTest")
class TransactionHistoryServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID DISBURSEMENT_ID = UUID.randomUUID();
    private static final UUID INSTALLMENT_ID_1 = UUID.randomUUID();
    private static final UUID INSTALLMENT_ID_2 = UUID.randomUUID();

    @Mock
    private LoanApplicationDisbursementRepository loanApplicationDisbursementRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    @Nested
    @DisplayName("getByCustomerId")
    class GetByCustomerIdTest {

        @Test
        @DisplayName("should return combined disbursement and paid installment transactions sorted by date descending")
        void shouldReturnSortedTransactionHistory() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime earliest = now.minusDays(10);

            LoanDisbursement disbursement = createDisbursement(DISBURSEMENT_ID, "REF-100", new BigDecimal("5000000"), earliest);
            LoanInstallment paidInstallment = createInstallment(INSTALLMENT_ID_1, "TRX-INS-123", new BigDecimal("1000000"), InstallmentStatus.PAID, now);
            LoanInstallment unpaidInstallment = createInstallment(INSTALLMENT_ID_2, "TRX-INS-123", new BigDecimal("1000000"), InstallmentStatus.UNPAID, null);

            when(loanApplicationDisbursementRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of(disbursement));
            when(loanInstallmentRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of(paidInstallment, unpaidInstallment));

            List<TransactionHistoryResponse> result = transactionHistoryService.getByCustomerId(CUSTOMER_ID);

            assertNotNull(result);
            assertEquals(2, result.size());

            assertEquals(INSTALLMENT_ID_1, result.get(0).getId());
            assertEquals(now, result.get(0).getDate());

            assertEquals(DISBURSEMENT_ID, result.get(1).getId());
            assertEquals(earliest, result.get(1).getDate());

            verify(loanApplicationDisbursementRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
            verify(loanInstallmentRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should return empty list when no disbursements or installments exist for customer")
        void shouldReturnEmptyListWhenNoTransactionsExist() {
            when(loanApplicationDisbursementRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of());
            when(loanInstallmentRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of());

            List<TransactionHistoryResponse> result = transactionHistoryService.getByCustomerId(CUSTOMER_ID);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(loanApplicationDisbursementRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
            verify(loanInstallmentRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should filter out unpaid installments and only return disbursements when no installments are paid")
        void shouldFilterOutUnpaidInstallments() {
            LocalDateTime disbursementDate = LocalDateTime.now().minusDays(1);
            LoanDisbursement disbursement = createDisbursement(DISBURSEMENT_ID, "REF-200", new BigDecimal("3000000"), disbursementDate);
            LoanInstallment unpaidInstallment = createInstallment(INSTALLMENT_ID_1, "TRX-INS-123", new BigDecimal("600000"), InstallmentStatus.UNPAID, null);

            when(loanApplicationDisbursementRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of(disbursement));
            when(loanInstallmentRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of(unpaidInstallment));

            List<TransactionHistoryResponse> result = transactionHistoryService.getByCustomerId(CUSTOMER_ID);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(DISBURSEMENT_ID, result.get(0).getId());

            verify(loanApplicationDisbursementRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
            verify(loanInstallmentRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
        }
    }

    private LoanDisbursement createDisbursement(UUID id, String reference, BigDecimal amount, LocalDateTime createdDate) {
        LoanDisbursement disbursement = new LoanDisbursement();
        disbursement.setId(id);
        disbursement.setTransactionReference(reference);
        disbursement.setDisbursementAmount(amount);
        disbursement.setCreatedDate(createdDate);
        return disbursement;
    }

    private LoanInstallment createInstallment(UUID id, String installmentNumber, BigDecimal paidAmount, InstallmentStatus status, LocalDateTime paidDate) {
        LoanInstallment installment = new LoanInstallment();
        installment.setId(id);
        installment.setInstallmentNumber(installmentNumber);
        installment.setPaidAmount(paidAmount);
        installment.setStatus(status);
        installment.setPaidDate(paidDate);
        return installment;
    }
}
