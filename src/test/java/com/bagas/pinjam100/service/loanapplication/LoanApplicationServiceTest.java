package com.bagas.pinjam100.service.loanapplication;

import com.bagas.pinjam100.dto.request.loanapplication.ApprovalRequest;
import com.bagas.pinjam100.dto.request.loanapplication.LoanApplicationRequest;
import com.bagas.pinjam100.dto.request.loanapplication.ReviewRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationApprovalResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationDisbursementResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationResponse;
import com.bagas.pinjam100.dto.response.loanapplication.LoanApplicationReviewResponse;
import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerDetail;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.entity.customer.Rekening;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.entity.loanapplication.*;
import com.bagas.pinjam100.entity.userrolepermission.User;
import com.bagas.pinjam100.repository.BranchRepository;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.customer.RekeningRepository;
import com.bagas.pinjam100.repository.installment.LoanInstallmentRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationApprovalRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationDisbursementRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationReviewRepository;
import com.bagas.pinjam100.service.auth.AuthService;
import com.bagas.pinjam100.service.customer.CustomerService;
import com.bagas.pinjam100.service.notification.NotificationService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanApplicationServiceTest")
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerLimitRepository customerLimitRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private LoanApplicationReviewRepository loanApplicationReviewRepository;
    @Mock
    private LoanApplicationApprovalRepository loanApplicationApprovalRepository;
    @Mock
    private LoanApplicationDisbursementRepository loanApplicationDisbursementRepository;
    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private RekeningRepository rekeningRepository;
    @Mock
    private AuthService authService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanApplicationService loanApplicationService;

    private UUID loanId;
    private UUID customerId;
    private UUID branchId;
    private Customer customer;
    private LoanApplication loanApplication;
    private CustomerLimit customerLimit;

    @BeforeEach
    void setUp() {
        loanId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        branchId = UUID.randomUUID();

        Branch branch = new Branch();
        branch.setId(branchId);
        branch.setCity("Kota Administrasi Jakarta Selatan");

        CustomerDetail detail = new CustomerDetail();
        detail.setCity("Kota Administrasi Jakarta Selatan");

        customer = new Customer();
        customer.setId(customerId);
        customer.setDetail(detail);

        loanApplication = new LoanApplication();
        loanApplication.setId(loanId);
        loanApplication.setCustomer(customer);
        loanApplication.setLoanAmount(BigDecimal.valueOf(5000000));
        loanApplication.setTenorMonths(6);
        loanApplication.setStatus(LoanApplicationStatus.UNDER_REVIEW);
        loanApplication.setBranch(branch);

        customerLimit = new CustomerLimit();
        customerLimit.setCustomer(customer);
        customerLimit.setAvailableLimit(BigDecimal.valueOf(10000000));
    }

    @Nested
    @DisplayName("findAll and findSpecific Methods")
    class FindMethodsTest {

        @Test
        @DisplayName("should find all active loan applications")
        void shouldFindAll() {
            when(loanApplicationRepository.findAllByDeletedDateIsNull())
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            List<LoanApplicationResponse> result = loanApplicationService.findAllByDeletedDateIsNull();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("should find all for review")
        void shouldFindAllForReview() {
            User user = new User();
            user.setBranch(loanApplication.getBranch());
            when(authService.getCurrentUser()).thenReturn(user);
            when(loanApplicationRepository.findAllByStatusAndBranch_IdAndDeletedDateIsNull(LoanApplicationStatus.UNDER_REVIEW, branchId))
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            List<LoanApplicationResponse> result = loanApplicationService.findAllForReview();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("should find all for approval")
        void shouldFindAllForApproval() {
            User user = new User();
            user.setBranch(loanApplication.getBranch());
            when(authService.getCurrentUser()).thenReturn(user);
            when(loanApplicationRepository.findAllByStatusAndBranch_IdAndDeletedDateIsNull(LoanApplicationStatus.PASS_REVIEW, branchId))
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            List<LoanApplicationResponse> result = loanApplicationService.findAllForApproval();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("should find all for disbursement")
        void shouldFindAllForDisbursement() {
            when(loanApplicationRepository.findAllByStatusAndDeletedDateIsNull(LoanApplicationStatus.APPROVED))
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            List<LoanApplicationResponse> result = loanApplicationService.findAllForDisbursement();
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("should find by branch id")
        void shouldFindByBranch() {
            when(loanApplicationRepository.findByBranch_IdAndDeletedDateIsNull(branchId))
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            List<LoanApplicationResponse> result = loanApplicationService.findByBranchAndDeletedDateIsNull(branchId);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("should find by customer id")
        void shouldFindByCustomer() {
            when(loanApplicationRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(List.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            List<LoanApplicationResponse> result = loanApplicationService.findByCustomerAndDeletedDateIsNull(customerId);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("should find by id for review")
        void shouldFindByIdForReview() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(customerService.findDetailById(customerId))
                    .thenReturn(new CustomerDetailResponse());

            LoanApplicationReviewResponse response = loanApplicationService.findByIdForReview(loanId);
            assertNotNull(response);
        }

        @Test
        @DisplayName("should throw exception when review not found for approval detail")
        void shouldThrowWhenReviewNotFound() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(customerService.findDetailById(customerId))
                    .thenReturn(new CustomerDetailResponse());
            when(loanApplicationReviewRepository.findByLoanApplication_Id(loanId))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> loanApplicationService.findByIdForApproval(loanId));
        }

        @Test
        @DisplayName("should find by id for approval successfully")
        void shouldFindByIdForApproval() {
            User reviewer = new User();
            reviewer.setId(UUID.randomUUID());

            LoanApplicationReview review = new LoanApplicationReview();
            review.setResult(ReviewResult.APPROVED);
            review.setLoanApplication(loanApplication);
            review.setReviewer(reviewer);

            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(customerService.findDetailById(customerId))
                    .thenReturn(new CustomerDetailResponse());
            when(loanApplicationReviewRepository.findByLoanApplication_Id(loanId))
                    .thenReturn(Optional.of(review));

            LoanApplicationApprovalResponse response = loanApplicationService.findByIdForApproval(loanId);
            assertNotNull(response);
        }

        @Test
        @DisplayName("should find by id for disbursement successfully")
        void shouldFindByIdForDisbursement() {
            Rekening rekening = new Rekening();
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));
            when(rekeningRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(rekening));

            LoanApplicationDisbursementResponse response = loanApplicationService.findByIdForDisbursement(loanId);
            assertNotNull(response);
        }

        @Test
        @DisplayName("should throw exception when rekening not found for disbursement detail")
        void shouldThrowWhenRekeningNotFoundForDisbursement() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));
            when(rekeningRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> loanApplicationService.findByIdForDisbursement(loanId));
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedDateIsNull")
    class FindByIdTest {

        @Test
        @DisplayName("should return loan application response when id exists")
        void shouldReturnLoanApplicationWhenFound() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.findByIdAndDeletedDateIsNull(loanId);

            assertNotNull(response);
            verify(loanApplicationRepository).findByIdAndDeletedDateIsNull(loanId);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when id not found")
        void shouldThrowExceptionWhenNotFound() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    loanApplicationService.findByIdAndDeletedDateIsNull(loanId)
            );
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("should save loan application successfully when request is valid")
        void shouldSaveLoanApplicationSuccessfully() {
            LoanApplicationRequest request = new LoanApplicationRequest();
            request.setCustomerId(customerId);
            request.setLoanAmount(BigDecimal.valueOf(5000000));
            request.setTenorMonths(6);
            request.setPurpose("Modal Usaha");

            when(customerRepository.findByIdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customer));
            when(branchRepository.findByCityAndDeletedDateIsNull(any()))
                    .thenReturn(Optional.of(loanApplication.getBranch()));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.save(request);

            assertNotNull(response);
            verify(loanApplicationRepository).saveAndFlush(any(LoanApplication.class));
            verify(customerLimitRepository).save(any(CustomerLimit.class));
        }

        @Test
        @DisplayName("should fallback branch to Jakarta Selatan when city branch not found")
        void shouldFallbackBranchWhenCityNotFound() {
            LoanApplicationRequest request = new LoanApplicationRequest();
            request.setCustomerId(customerId);
            request.setLoanAmount(BigDecimal.valueOf(5000000));
            request.setTenorMonths(6);
            request.setPurpose("Modal Usaha");

            Branch fallbackBranch = new Branch();
            fallbackBranch.setCity("Kota Administrasi Jakarta Selatan");

            when(customerRepository.findByIdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customer));
            when(branchRepository.findByCityAndDeletedDateIsNull(customer.getDetail().getCity()))
                    .thenReturn(Optional.empty());
            when(branchRepository.findByCityAndDeletedDateIsNull("Kota Administrasi Jakarta Selatan"))
                    .thenReturn(Optional.of(fallbackBranch));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.save(request);
            assertNotNull(response);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when fallback branch is also not found")
        void shouldThrowWhenFallbackBranchNotFound() {
            LoanApplicationRequest request = new LoanApplicationRequest();
            request.setCustomerId(customerId);
            request.setLoanAmount(BigDecimal.valueOf(5000000));
            request.setTenorMonths(6);

            when(customerRepository.findByIdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customer));
            when(branchRepository.findByCityAndDeletedDateIsNull(any()))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> loanApplicationService.save(request));
        }

        @Test
        @DisplayName("should throw IllegalStateException when customer limit is missing on save")
        void shouldThrowWhenLimitMissingOnSave() {
            LoanApplicationRequest request = new LoanApplicationRequest();
            request.setCustomerId(customerId);
            request.setLoanAmount(BigDecimal.valueOf(5000000));
            request.setTenorMonths(6);

            when(customerRepository.findByIdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customer));
            when(branchRepository.findByCityAndDeletedDateIsNull(any()))
                    .thenReturn(Optional.of(loanApplication.getBranch()));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalStateException.class, () -> loanApplicationService.save(request));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when loan amount exceeds available limit")
        void shouldThrowWhenExceedsLimit() {
            LoanApplicationRequest request = new LoanApplicationRequest();
            request.setCustomerId(customerId);
            request.setLoanAmount(BigDecimal.valueOf(15000000)); // Melebihi limit 10 juta
            request.setTenorMonths(6);

            customerLimit.setAvailableLimit(BigDecimal.valueOf(5000000));

            when(customerRepository.findByIdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customer));
            when(branchRepository.findByCityAndDeletedDateIsNull(any()))
                    .thenReturn(Optional.of(loanApplication.getBranch()));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            assertThrows(IllegalArgumentException.class, () -> loanApplicationService.save(request));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when loan amount is below minimum")
        void shouldThrowExceptionWhenAmountTooLow() {
            LoanApplicationRequest request = new LoanApplicationRequest();
            request.setLoanAmount(BigDecimal.valueOf(100000));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> loanApplicationService.save(request)
            );
            assertTrue(exception.getMessage().contains("Minimum pinjaman"));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when loan amount exceeds maximum")
        void shouldThrowExceptionWhenAmountTooHigh() {
            LoanApplicationRequest request = new LoanApplicationRequest();
            request.setLoanAmount(BigDecimal.valueOf(40000000));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> loanApplicationService.save(request)
            );
            assertTrue(exception.getMessage().contains("Maksimum pinjaman"));
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteApplicationTest {

        @Test
        @DisplayName("should delete loan application successfully")
        void shouldDeleteSuccessfully() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.delete(loanId);
            assertNotNull(response);
            verify(loanApplicationRepository).save(loanApplication);
        }

        @Test
        @DisplayName("should throw exception when deleting non-existent application")
        void shouldThrowWhenDeletingNotFound() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> loanApplicationService.delete(loanId));
        }
    }

    @Nested
    @DisplayName("review")
    class ReviewTest {

        @Test
        @DisplayName("should update status to PASS_REVIEW when approved by marketing")
        void shouldPassReviewSuccessfully() {
            ReviewRequest request = new ReviewRequest();
            request.setReviewResult(ReviewResult.APPROVED);
            request.setNotes("Dokumen lengkap");

            User reviewer = new User();
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(authService.getCurrentUser()).thenReturn(reviewer);
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.review(loanId, request);

            assertNotNull(response);
            assertEquals(LoanApplicationStatus.PASS_REVIEW, loanApplication.getStatus());
            verify(loanApplicationReviewRepository).save(any(LoanApplicationReview.class));
        }

        @Test
        @DisplayName("should update status to REJECT_REVIEW when failed by marketing")
        void shouldRejectReviewSuccessfully() {
            ReviewRequest request = new ReviewRequest();
            request.setReviewResult(ReviewResult.FAILED);
            request.setNotes("Dokumen tidak valid");

            User reviewer = new User();
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(authService.getCurrentUser()).thenReturn(reviewer);
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.review(loanId, request);

            assertNotNull(response);
            assertEquals(LoanApplicationStatus.REJECT_REVIEW, loanApplication.getStatus());
        }

        @Test
        @DisplayName("should throw exception when reviewing non-existent application")
        void shouldThrowWhenReviewNotFound() {
            ReviewRequest request = new ReviewRequest();
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> loanApplicationService.review(loanId, request));
        }
    }

    @Nested
    @DisplayName("approve")
    class ApproveTest {

        @Test
        @DisplayName("should update status to APPROVED when manager approves")
        void shouldApproveSuccessfully() {
            ApprovalRequest request = new ApprovalRequest();
            request.setApprovalStatus(ApprovalStatus.APPROVED);
            request.setNotes("Disetujui manajer");

            User approver = new User();
            loanApplication.setStatus(LoanApplicationStatus.PASS_REVIEW);

            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(authService.getCurrentUser()).thenReturn(approver);
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.approve(loanId, request);

            assertNotNull(response);
            assertEquals(LoanApplicationStatus.APPROVED, loanApplication.getStatus());
            verify(loanApplicationApprovalRepository).save(any(LoanApplicationApproval.class));
        }

        @Test
        @DisplayName("should update status to REJECTED when manager rejects")
        void shouldRejectSuccessfully() {
            ApprovalRequest request = new ApprovalRequest();
            request.setApprovalStatus(ApprovalStatus.REJECTED);
            request.setNotes("Ditolak manajer");

            User approver = new User();
            loanApplication.setStatus(LoanApplicationStatus.PASS_REVIEW);

            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));
            when(authService.getCurrentUser()).thenReturn(approver);
            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response = loanApplicationService.approve(loanId, request);

            assertNotNull(response);
            assertEquals(LoanApplicationStatus.REJECTED, loanApplication.getStatus());
        }

        @Test
        @DisplayName("should throw exception when approving non-existent application")
        void shouldThrowWhenApproveNotFound() {
            ApprovalRequest request = new ApprovalRequest();
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> loanApplicationService.approve(loanId, request));
        }
    }

    @Nested
    @DisplayName("disburse")
    class DisburseTest {

        @Test
        @DisplayName("should disburse loan, create installments and send notification successfully")
        void shouldDisburseSuccessfully() {
            Rekening rekening = new Rekening();
            rekening.setId(UUID.randomUUID());

            loanApplication.setStatus(LoanApplicationStatus.APPROVED);
            loanApplication.setTenorMonths(12);
            loanApplication.setInstallmentAmount(BigDecimal.valueOf(900000));

            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.of(loanApplication));

            when(rekeningRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(rekening));

            when(customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(customerId))
                    .thenReturn(Optional.of(customerLimit));

            LoanApplicationResponse response =
                    loanApplicationService.disburse(loanId);

            assertNotNull(response);
            assertEquals(
                    LoanApplicationStatus.DISBURSED,
                    loanApplication.getStatus()
            );

            verify(loanApplicationDisbursementRepository)
                    .save(any(LoanDisbursement.class));

            verify(loanInstallmentRepository, times(12))
                    .save(any(LoanInstallment.class));

            verify(notificationService)
                    .sendToCustomer(
                            any(),
                            any(),
                            any(),
                            any(),
                            any()
                    );

            verify(customerLimitRepository)
                    .findByCustomer_IdAndDeletedDateIsNull(customerId);
        }

        @Test
        @DisplayName("should throw exception when disburse application not found")
        void shouldThrowWhenDisburseNotFound() {
            when(loanApplicationRepository.findByIdAndDeletedDateIsNull(loanId))
                    .thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> loanApplicationService.disburse(loanId)
            );

            verify(loanApplicationDisbursementRepository, never())
                    .save(any(LoanDisbursement.class));

            verify(loanInstallmentRepository, never())
                    .save(any(LoanInstallment.class));

            verify(notificationService, never())
                    .sendToCustomer(any(), any(), any(), any(), any());
        }
    }
}