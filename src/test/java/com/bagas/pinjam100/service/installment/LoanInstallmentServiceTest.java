package com.bagas.pinjam100.service.installment;

import com.bagas.pinjam100.dto.response.installment.LoanInstallmentResponse;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.repository.installment.LoanInstallmentRepository;
import com.bagas.pinjam100.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanInstallmentServiceTest")
class LoanInstallmentServiceTest {

    private static final UUID INSTALLMENT_ID = UUID.randomUUID();
    private static final UUID LOAN_APPLICATION_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final BigDecimal INSTALLMENT_AMOUNT = new BigDecimal("500000");

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanInstallmentService loanInstallmentService;

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("should return loan installment response when found")
        void shouldReturnLoanInstallmentResponseWhenFound() {
            LoanInstallment installment = createLoanInstallment();

            when(loanInstallmentRepository.findById(INSTALLMENT_ID))
                    .thenReturn(Optional.of(installment));

            LoanInstallmentResponse result = loanInstallmentService.getById(INSTALLMENT_ID);

            assertNotNull(result);
            verify(loanInstallmentRepository).findById(INSTALLMENT_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when installment not found by id")
        void shouldThrowExceptionWhenNotFound() {
            when(loanInstallmentRepository.findById(INSTALLMENT_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> loanInstallmentService.getById(INSTALLMENT_ID)
            );

            assertEquals("Angsuran tidak ditemukan", exception.getMessage());
            verify(loanInstallmentRepository).findById(INSTALLMENT_ID);
        }
    }

    @Nested
    @DisplayName("getByLoanApplication_Id")
    class GetByLoanApplicationIdTest {

        @Test
        @DisplayName("should return list of installment responses by loan application id")
        void shouldReturnListOfInstallmentsByLoanApplicationId() {
            LoanInstallment installment1 = createLoanInstallment();
            LoanInstallment installment2 = createLoanInstallment();

            when(loanInstallmentRepository.findByLoanApplication_Id(LOAN_APPLICATION_ID))
                    .thenReturn(List.of(installment1, installment2));

            List<LoanInstallmentResponse> result = loanInstallmentService.getByLoanApplication_Id(LOAN_APPLICATION_ID);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(loanInstallmentRepository).findByLoanApplication_Id(LOAN_APPLICATION_ID);
        }

        @Test
        @DisplayName("should return empty list when no installments found for loan application id")
        void shouldReturnEmptyListWhenNoneFound() {
            when(loanInstallmentRepository.findByLoanApplication_Id(LOAN_APPLICATION_ID))
                    .thenReturn(List.of());

            List<LoanInstallmentResponse> result = loanInstallmentService.getByLoanApplication_Id(LOAN_APPLICATION_ID);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(loanInstallmentRepository).findByLoanApplication_Id(LOAN_APPLICATION_ID);
        }
    }

    @Nested
    @DisplayName("getByCustomerId")
    class GetByCustomerIdTest {

        @Test
        @DisplayName("should return list of installment responses by customer id")
        void shouldReturnListOfInstallmentsByCustomerId() {
            LoanInstallment installment = createLoanInstallment();

            when(loanInstallmentRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of(installment));

            List<LoanInstallmentResponse> result = loanInstallmentService.getByCustomerId(CUSTOMER_ID);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(loanInstallmentRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
        }

        @Test
        @DisplayName("should return empty list when no installments found for customer id")
        void shouldReturnEmptyListWhenNoneFoundForCustomer() {
            when(loanInstallmentRepository.findByLoanApplication_Customer_Id(CUSTOMER_ID))
                    .thenReturn(List.of());

            List<LoanInstallmentResponse> result = loanInstallmentService.getByCustomerId(CUSTOMER_ID);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(loanInstallmentRepository).findByLoanApplication_Customer_Id(CUSTOMER_ID);
        }
    }

    @Nested
    @DisplayName("pay")
    class PayTest {

        @Test
        @DisplayName("should update installment status to PAID and send notification successfully")
        void shouldPayInstallmentSuccessfully() {
            LoanInstallment installment = createLoanInstallment();

            when(loanInstallmentRepository.findById(INSTALLMENT_ID))
                    .thenReturn(Optional.of(installment));
            when(loanInstallmentRepository.save(any(LoanInstallment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            LoanInstallmentResponse result = loanInstallmentService.pay(INSTALLMENT_ID);

            assertNotNull(result);
            assertEquals(INSTALLMENT_AMOUNT, installment.getPaidAmount());
            assertEquals(InstallmentStatus.PAID, installment.getStatus());
            assertNotNull(installment.getPaidDate());

            verify(loanInstallmentRepository).findById(INSTALLMENT_ID);
            verify(loanInstallmentRepository).save(installment);
            verify(notificationService).sendToCustomer(
                    eq(installment.getLoanApplication().getCustomer()),
                    eq("Pembayaran Angsuran Berhasil"),
                    eq("Pembayaran angsuran Anda telah berhasil diterima. Terima kasih atas pembayaran yang telah dilakukan."),
                    eq("installment"),
                    eq("pinjam100://installment/" + INSTALLMENT_ID)
            );
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when installment to pay not found")
        void shouldThrowExceptionWhenInstallmentToPayNotFound() {
            when(loanInstallmentRepository.findById(INSTALLMENT_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> loanInstallmentService.pay(INSTALLMENT_ID)
            );

            assertEquals("Angsuran tidak ditemukan", exception.getMessage());
            verify(loanInstallmentRepository, never()).save(any());
            verifyNoInteractions(notificationService);
        }
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setFullName("Bagas Aditya");
        return customer;
    }

    private LoanApplication createLoanApplication() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(LOAN_APPLICATION_ID);
        loanApplication.setCustomer(createCustomer());
        return loanApplication;
    }

    private LoanInstallment createLoanInstallment() {
        LoanInstallment installment = new LoanInstallment();
        installment.setId(INSTALLMENT_ID);
        installment.setLoanApplication(createLoanApplication());
        installment.setInstallmentAmount(INSTALLMENT_AMOUNT);
        installment.setStatus(InstallmentStatus.UNPAID);
        return installment;
    }
}
