package com.bagas.pinjam100.service.loanapplication;

import com.bagas.pinjam100.dto.request.loanapplication.ApprovalRequest;
import com.bagas.pinjam100.dto.request.loanapplication.DisbursementRequest;
import com.bagas.pinjam100.dto.request.loanapplication.LoanApplicationRequest;
import com.bagas.pinjam100.dto.request.loanapplication.ReviewRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.dto.response.loanapplication.*;
import com.bagas.pinjam100.entity.customer.*;
import com.bagas.pinjam100.entity.loanapplication.*;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.RekeningRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationApprovalRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationDisbursementRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationReviewRepository;
import com.bagas.pinjam100.repository.userrolepermission.UserRepository;
import com.bagas.pinjam100.service.auth.AuthService;
import com.bagas.pinjam100.service.customer.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class LoanApplicationService {
    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerLimitRepository customerLimitRepository;
    private final CustomerService customerService;
    private final LoanApplicationReviewRepository loanApplicationReviewRepository;
    private final LoanApplicationApprovalRepository loanApplicationApprovalRepository;
    private final LoanApplicationDisbursementRepository loanApplicationDisbursementRepository;
    private final AuthService authService;

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

    public LoanApplicationApprovalResponse findByIdForApproval(UUID id) {
        LoanApplication response = loanApplicationRepository
                .findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Aplikasi pinjaman tidak ditemukan")
                );

        CustomerDetailResponse customer =
                customerService.findDetailById(response.getCustomer().getId());

        return new LoanApplicationApprovalResponse(response, customer);
    }

    public LoanApplicationDisbursementResponse findByIdForDisbursement(UUID id) {
        LoanApplication response = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi pinjaman tidak ditemukan"));

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(response.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationDisbursementResponse(
                response,
                customerLimit
        );
    }

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

    public LoanApplicationResponse save(LoanApplicationRequest request) {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setApplicationId(UUID.randomUUID().toString());
        loanApplication.setBranch(request.getBranch());
        loanApplication.setLoanAmount(request.getLoanAmount());
        loanApplication.setTenorMonths(request.getTenor_months());
        loanApplication.setInterestRate(request.getInterestRate());
        loanApplication.setPurpose(request.getPurpose());

        loanApplicationRepository.save(loanApplication);

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }

    public LoanApplicationResponse update(UUID id, LoanApplicationRequest request) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Aplikasi pinjaman tidak ditemukan"));

        loanApplication.setBranch(request.getBranch());
        loanApplication.setLoanAmount(request.getLoanAmount());
        loanApplication.setTenorMonths(request.getTenor_months());
        loanApplication.setInterestRate(request.getInterestRate());
        loanApplication.setPurpose(request.getPurpose());
        loanApplicationRepository.save(loanApplication);

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }

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
        disbursement.setRekening(loanApplication.getRekening());
        loanApplicationDisbursementRepository.save(disbursement);

        CustomerLimit customerLimit = customerLimitRepository
                .findByCustomer_IdAndDeletedDateIsNull(loanApplication.getCustomer().getId())
                .orElse(null);

        return new LoanApplicationResponse(
                loanApplication,
                customerLimit
        );
    }
}