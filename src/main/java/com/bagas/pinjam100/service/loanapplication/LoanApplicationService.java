package com.bagas.pinjam100.service.loanapplication;

import com.bagas.pinjam100.config.loan.LoanConfig;
import com.bagas.pinjam100.dto.request.loanapplication.ApprovalRequest;
import com.bagas.pinjam100.dto.request.loanapplication.DisbursementRequest;
import com.bagas.pinjam100.dto.request.loanapplication.LoanApplicationRequest;
import com.bagas.pinjam100.dto.request.loanapplication.ReviewRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.dto.response.loanapplication.*;
import com.bagas.pinjam100.entity.customer.*;
import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.entity.loanapplication.*;
import com.bagas.pinjam100.repository.BranchRepository;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.customer.RekeningRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationApprovalRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationDisbursementRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationReviewRepository;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import com.bagas.pinjam100.service.auth.AuthService;
import com.bagas.pinjam100.service.customer.CustomerService;
import com.bagas.pinjam100.service.dashboard.DashboardService;
import com.bagas.pinjam100.service.installment.LoanInstallmentService;
import com.bagas.pinjam100.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class LoanApplicationService {

    public static final String CACHE_LOAN = "loan_application";
    public static final String CACHE_LOAN_ALL = "loan_application_all";

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;
    private final CustomerLimitRepository customerLimitRepository;
    private final CustomerService customerService;
    private final LoanApplicationReviewRepository loanApplicationReviewRepository;
    private final LoanApplicationApprovalRepository loanApplicationApprovalRepository;
    private final LoanApplicationDisbursementRepository loanApplicationDisbursementRepository;
    private final BranchRepository branchRepository;
    private final RekeningRepository rekeningRepository;
    private final AuthService authService;
    private final NotificationService notificationService;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Cacheable(cacheNames = CACHE_LOAN_ALL, key = "'all'")
    public List<LoanApplicationResponse> findAllByDeletedDateIsNull() {
        return loanApplicationRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();
    }

    @Cacheable(cacheNames = CACHE_LOAN, key = "#id")
    public LoanApplicationResponse findByIdAndDeletedDateIsNull(UUID id) {
        LoanApplication response = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi pinjaman tidak ditemukan"));

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(response.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                response,
                customerLimit
        );
    }

    @Cacheable(cacheNames = CACHE_LOAN_ALL, key = "'review_branch_' + @authService.getCurrentUser().getBranch().getId()")
    public List<LoanApplicationResponse> findAllForReview() {
        return loanApplicationRepository.findAllByStatusAndBranch_IdAndDeletedDateIsNull(LoanApplicationStatus.UNDER_REVIEW, authService.getCurrentUser().getBranch().getId())
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();
    }

    @Cacheable(cacheNames = CACHE_LOAN_ALL, key = "'approval_branch_' + @authService.getCurrentUser().getBranch().getId()")
    public List<LoanApplicationResponse> findAllForApproval() {
        return loanApplicationRepository.findAllByStatusAndBranch_IdAndDeletedDateIsNull(LoanApplicationStatus.PASS_REVIEW, authService.getCurrentUser().getBranch().getId())
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();
    }

    @Cacheable(cacheNames = CACHE_LOAN_ALL, key = "'disbursement'")
    public List<LoanApplicationResponse> findAllForDisbursement() {
        return loanApplicationRepository.findAllByStatusAndDeletedDateIsNull(LoanApplicationStatus.APPROVED)
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();
    }

    @Cacheable(cacheNames = CACHE_LOAN, key = "'review_' + #id")
    public LoanApplicationReviewResponse findByIdForReview(UUID id) {
        LoanApplication response = loanApplicationRepository
                .findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Aplikasi pinjaman tidak ditemukan")
                );

        CustomerDetailResponse customer =
                customerService.findDetailById(response.getCustomer().getId());

        return new LoanApplicationReviewResponse(response, customer);
    }

    @Cacheable(cacheNames = CACHE_LOAN, key = "'approval_' + #id")
    public LoanApplicationApprovalResponse findByIdForApproval(UUID id) {
        LoanApplication response = loanApplicationRepository
                .findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Aplikasi pinjaman tidak ditemukan")
                );

        CustomerDetailResponse customer =
                customerService.findDetailById(response.getCustomer().getId());

        LoanApplicationReview review = loanApplicationReviewRepository
                .findByLoanApplication_Id(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Aplikasi belum dilakukan review oleh marketing")
                );

        return new LoanApplicationApprovalResponse(response, customer, new ReviewResponse(review));
    }

    @Cacheable(cacheNames = CACHE_LOAN, key = "'disbursement_' + #id")
    public LoanApplicationDisbursementResponse findByIdForDisbursement(UUID id) {
        LoanApplication response = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi pinjaman tidak ditemukan"));

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(response.getCustomer().getId())
                .orElse(null);

        Rekening rekening = rekeningRepository
                .findByCustomer_IdAndDeletedDateIsNull(response.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException("Rekening tidak ditemukan"));

        return new LoanApplicationDisbursementResponse(
                response,
                customerLimit,
                rekening
        );
    }

    @Cacheable(cacheNames = CACHE_LOAN_ALL, key = "'branch_' + #id")
    public List<LoanApplicationResponse> findByBranchAndDeletedDateIsNull(UUID id) {
        return loanApplicationRepository.findByBranch_IdAndDeletedDateIsNull(id)
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();
    }

    @Cacheable(cacheNames = CACHE_LOAN_ALL, key = "'customer_' + #id")
    public List<LoanApplicationResponse> findByCustomerAndDeletedDateIsNull(UUID id) {
        return loanApplicationRepository.findByCustomer_IdAndDeletedDateIsNull(id)
                .stream()
                .map(loanApplication -> new LoanApplicationResponse(
                        loanApplication,
                        customerLimitRepository
                                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                                .orElse(null)
                ))
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_LOAN_ALL, allEntries = true),
            @CacheEvict(cacheNames = "customer_limit", key = "'customer_' + #request.customerId"),
            @CacheEvict(cacheNames = "customer_detail", key = "#request.customerId"),
            @CacheEvict(cacheNames = DashboardService.CACHE_DASHBOARD, allEntries = true)
    })
    public LoanApplicationResponse save(LoanApplicationRequest request) {
        if (request.getLoanAmount().compareTo(LoanConfig.MIN_LOAN_AMOUNT) < 0) {
            throw new IllegalArgumentException(
                    "Minimum pinjaman adalah Rp500.000"
            );
        }

        if (request.getLoanAmount().compareTo(LoanConfig.MAX_LOAN_AMOUNT) > 0) {
            throw new IllegalArgumentException(
                    "Maksimum pinjaman adalah Rp35.000.000"
            );
        }


        Customer customer = customerRepository.findByIdAndDeletedDateIsNull(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer tidak ditemukan"));

        LoanApplication loanApplication = new LoanApplication();
        String applicationId = "APP-" +
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) +
                "-" +
                String.format("%010d", RANDOM.nextInt(1_000_000_000));
        loanApplication.setApplicationId(applicationId);
        loanApplication.setStatus(LoanApplicationStatus.UNDER_REVIEW);
        loanApplication.setLoanAmount(request.getLoanAmount());
        loanApplication.setTenorMonths(request.getTenorMonths());
        loanApplication.setPurpose(request.getPurpose());
        loanApplication.setInterestRate(LoanConfig.DAILY_INTEREST_RATE);

        BigDecimal totalInterest = loanApplication.getLoanAmount()
                .multiply(loanApplication.getInterestRate())
                .multiply(BigDecimal.valueOf(LoanConfig.DAYS_PER_MONTH))
                .multiply(BigDecimal.valueOf(loanApplication.getTenorMonths()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalPayment = loanApplication.getLoanAmount()
                .add(totalInterest);

        BigDecimal installmentAmount = totalPayment
                .divide(
                        BigDecimal.valueOf(loanApplication.getTenorMonths()),
                        2,
                        RoundingMode.HALF_UP
                );

        loanApplication.setInstallmentAmount(installmentAmount);
        loanApplication.setCustomer(customer);
        loanApplication.setBranch(
                branchRepository.findByCityAndDeletedDateIsNull(
                        customer.getDetail().getCity()
                ).orElseGet(() ->
                        branchRepository.findByCityAndDeletedDateIsNull(
                                "Kota Administrasi Jakarta Selatan"
                        ).orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Branch Kota Administrasi Jakarta Selatan tidak ditemukan"
                                )
                        )
                )
        );

        loanApplicationRepository.saveAndFlush(loanApplication);

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(customer.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Customer belum memiliki limit"
                ));

        if (request.getLoanAmount().compareTo(customerLimit.getAvailableLimit()) > 0) {
            throw new IllegalArgumentException(
                    "Pinjaman melebihi limit yang tersedia"
            );
        }

        customerLimit.setAvailableLimit(
                customerLimit.getAvailableLimit().subtract(request.getLoanAmount())
        );
        customerLimitRepository.save(customerLimit);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_LOAN, key = "#id"),
            @CacheEvict(cacheNames = CACHE_LOAN, key = "'review_' + #id"),
            @CacheEvict(cacheNames = CACHE_LOAN, key = "'approval_' + #id"),
            @CacheEvict(cacheNames = CACHE_LOAN, key = "'disbursement_' + #id"),
            @CacheEvict(cacheNames = CACHE_LOAN_ALL, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.CACHE_DASHBOARD, allEntries = true)
    })
    public LoanApplicationResponse delete(UUID id) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi Pinjaman tidak ditemukan"));

        loanApplication.setDeletedDate(LocalDateTime.now());
        loanApplicationRepository.save(loanApplication);

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_LOAN, key = "#id"),
            @CacheEvict(cacheNames = CACHE_LOAN, key = "'review_' + #id"),
            @CacheEvict(cacheNames = CACHE_LOAN_ALL, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.CACHE_DASHBOARD, allEntries = true)
    })
    public LoanApplicationResponse review(UUID id, ReviewRequest request) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi Pinjaman tidak ditemukan"));

        if (ReviewResult.valueOf(request.getReviewResult().name()).equals(ReviewResult.APPROVED)) {
            loanApplication.setStatus(LoanApplicationStatus.PASS_REVIEW);
        } else if (ReviewResult.valueOf(request.getReviewResult().name()).equals(ReviewResult.FAILED)) {
            loanApplication.setStatus(LoanApplicationStatus.REJECT_REVIEW);
        }

        LoanApplicationReview review = new LoanApplicationReview();
        review.setLoanApplication(loanApplication);
        review.setResult(ReviewResult.valueOf(request.getReviewResult().name()));
        review.setNotes(request.getNotes());
        review.setReviewer(authService.getCurrentUser());
        loanApplicationReviewRepository.save(review);

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_LOAN, key = "#id"),
            @CacheEvict(cacheNames = CACHE_LOAN, key = "'approval_' + #id"),
            @CacheEvict(cacheNames = CACHE_LOAN_ALL, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.CACHE_DASHBOARD, allEntries = true)
    })
    public LoanApplicationResponse approve(UUID id, ApprovalRequest request) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi Pinjaman tidak ditemukan"));

        if (ApprovalStatus.valueOf(request.getApprovalStatus().name()).equals(ApprovalStatus.APPROVED)) {
            loanApplication.setStatus(LoanApplicationStatus.APPROVED);
        } else if (ApprovalStatus.valueOf(request.getApprovalStatus().name()).equals(ApprovalStatus.REJECTED)) {
            loanApplication.setStatus(LoanApplicationStatus.REJECTED);
        }

        LoanApplicationApproval approval = new LoanApplicationApproval();
        approval.setLoanApplication(loanApplication);
        approval.setStatus(ApprovalStatus.valueOf(request.getApprovalStatus().name()));
        approval.setNotes(request.getNotes());
        approval.setApprover(authService.getCurrentUser());
        loanApplicationApprovalRepository.save(approval);

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_LOAN, key = "#id"),
            @CacheEvict(cacheNames = CACHE_LOAN, key = "'disbursement_' + #id"),
            @CacheEvict(cacheNames = CACHE_LOAN_ALL, allEntries = true),
            @CacheEvict(cacheNames = LoanInstallmentService.CACHE_INSTALLMENT_APPLICATION, key = "#id"),
            @CacheEvict(cacheNames = LoanInstallmentService.CACHE_INSTALLMENT_CUSTOMER, allEntries = true),
            @CacheEvict(cacheNames = DashboardService.CACHE_DASHBOARD, allEntries = true)
    })
    public LoanApplicationResponse disburse(UUID id) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi Pinjaman ditemukan"));

        loanApplication.setStatus(LoanApplicationStatus.DISBURSED);

        LoanDisbursement disbursement = new LoanDisbursement();
        disbursement.setLoanApplication(loanApplication);
        disbursement.setDisbursementAmount(loanApplication.getLoanAmount());
        disbursement.setTransactionReference(
                "DTRX-" + String.format("%010d", new Random().nextLong(10_000_000_000L))
        );
        disbursement.setRekening(
                rekeningRepository.findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Rekening tidak ditemukan"))
        );
        loanApplicationDisbursementRepository.save(disbursement);

        SecureRandom random = new SecureRandom();
        LocalDate disbursementDate = LocalDate.now();

        for (int i = 0; i < loanApplication.getTenorMonths(); i++) {
            LoanInstallment loanInstallment = new LoanInstallment();
            loanInstallment.setLoanApplication(loanApplication);
            loanInstallment.setInstallmentNumber(
                    "TRX-INS-" +
                            disbursementDate.format(DateTimeFormatter.BASIC_ISO_DATE) +
                            "-" +
                            String.format("%010d", random.nextLong(10_000_000_000L))
            );
            loanInstallment.setInstallmentSequence(i + 1);
            loanInstallment.setDueDate(disbursementDate.plusMonths(i + 1L));
            loanInstallment.setInstallmentAmount(loanApplication.getInstallmentAmount());
            loanInstallment.setStatus(InstallmentStatus.UNPAID);
        }

        notificationService.sendToCustomer(
                loanApplication.getCustomer(),
                "Pinjaman Berhasil Dicairkan",
                "Pinjaman Anda telah berhasil dicairkan dan dana telah dikirimkan ke rekening Anda.",
                "disbursement",
                "pinjam100://disbursement/" + disbursement.getId()
        );

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }
}