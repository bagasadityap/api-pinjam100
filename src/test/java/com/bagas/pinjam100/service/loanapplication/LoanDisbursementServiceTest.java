package com.bagas.pinjam100.service.loanapplication;

import com.bagas.pinjam100.dto.response.loanapplication.DisbursementResponse;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationDisbursementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanDisbursementServiceTest")
class LoanDisbursementServiceTest {

    @Mock
    private LoanApplicationDisbursementRepository loanApplicationDisbursementRepository;

    @InjectMocks
    private LoanDisbursementService loanDisbursementService;

    private UUID disbursementId;
    private LoanDisbursement loanDisbursement;

    @BeforeEach
    void setUp() {
        disbursementId = UUID.randomUUID();

        // Buat mock data LoanApplication & Customer agar constructor DisbursementResponse tidak NullPointerException
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setCustomer(customer);
        loanApplication.setLoanAmount(BigDecimal.valueOf(5000000));

        loanDisbursement = new LoanDisbursement();
        loanDisbursement.setId(disbursementId);
        loanDisbursement.setLoanApplication(loanApplication);
        loanDisbursement.setDisbursementAmount(BigDecimal.valueOf(5000000));
        loanDisbursement.setTransactionReference("DTRX-1234567890");
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("should return disbursement response when disbursementId exists")
        void shouldReturnDisbursementWhenFound() {
            when(loanApplicationDisbursementRepository.findById(disbursementId))
                    .thenReturn(Optional.of(loanDisbursement));

            DisbursementResponse response = loanDisbursementService.getById(disbursementId);

            assertNotNull(response);
            verify(loanApplicationDisbursementRepository).findById(disbursementId);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when disbursementId not found")
        void shouldThrowExceptionWhenNotFound() {
            when(loanApplicationDisbursementRepository.findById(disbursementId))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> loanDisbursementService.getById(disbursementId)
            );

            assertEquals("Disbursement data tidak ditemukan", exception.getMessage());
            verify(loanApplicationDisbursementRepository).findById(disbursementId);
        }
    }
}