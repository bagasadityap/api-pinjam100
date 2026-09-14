package com.bagas.pinjam100.controller.loanapplication;

import com.bagas.pinjam100.dto.request.loanapplication.ApprovalRequest;
import com.bagas.pinjam100.dto.request.loanapplication.DisbursementRequest;
import com.bagas.pinjam100.dto.request.loanapplication.LoanApplicationRequest;
import com.bagas.pinjam100.dto.request.loanapplication.ReviewRequest;
import com.bagas.pinjam100.dto.response.loanapplication.*;
import com.bagas.pinjam100.entity.loanapplication.LoanApplication;
import com.bagas.pinjam100.service.loanapplication.LoanApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loan-application")
public class  LoanApplicationController {
    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @GetMapping
    public List<LoanApplicationResponse> getAll() {
        return loanApplicationService.findAllByDeletedDateIsNull();
    }

    @GetMapping("/review")
    public List<LoanApplicationResponse> getAllForReview() {
        return loanApplicationService.findAllForReview();
    }

    @GetMapping("/approval")
    public List<LoanApplicationResponse> getAllForApproval() {
        return loanApplicationService.findAllForApproval();
    }

    @GetMapping("/disbursement")
    public List<LoanApplicationResponse> getAllForDisbursement() {
        return loanApplicationService.findAllForDisbursement();
    }

    @GetMapping("/{id}")
    public LoanApplicationResponse getById(@PathVariable UUID id) {
        return loanApplicationService.findByIdAndDeletedDateIsNull(id);
    }

    @GetMapping("/{id}/review")
    public LoanApplicationReviewResponse getByIdReview(@PathVariable UUID id) {
        return loanApplicationService.findByIdForReview(id);
    }

    @PostMapping("/{id}/review")
    public LoanApplicationResponse review(@PathVariable UUID id, @RequestBody ReviewRequest reviewRequest) {
        return loanApplicationService.review(id, reviewRequest);
    }

    @GetMapping("/{id}/approval")
    public LoanApplicationApprovalResponse getByIdApproval(@PathVariable UUID id) {
        return loanApplicationService.findByIdForApproval(id);
    }

    @PostMapping("/{id}/approval")
    public LoanApplicationResponse approve(@PathVariable UUID id, @RequestBody ApprovalRequest approvalRequest) {
        return loanApplicationService.approve(id, approvalRequest);
    }

    @GetMapping("/{id}/disbursement")
    public LoanApplicationDisbursementResponse getByIdDisbursement(@PathVariable UUID id) {
        return loanApplicationService.findByIdForDisbursement(id);
    }

    @PostMapping("/{id}/disbursement")
    public LoanApplicationResponse disburse(@PathVariable UUID id) {
        return loanApplicationService.disburse(id);
    }

    @GetMapping("/{id}/branch")
    public List<LoanApplicationResponse> getByBranch(@PathVariable UUID id) {
        return loanApplicationService.findByBranchAndDeletedDateIsNull(id);
    }

    @GetMapping("/{id}/customer")
    public List<LoanApplicationResponse> getByCustomer(@PathVariable UUID id) {
        return loanApplicationService.findByCustomerAndDeletedDateIsNull(id);
    }

    @PostMapping
    public LoanApplicationResponse create(@RequestBody LoanApplicationRequest loanApplicationRequest) {
        return loanApplicationService.save(loanApplicationRequest);
    }

    @PutMapping("/{id}")
    public LoanApplicationResponse update(@PathVariable UUID id, @RequestBody LoanApplicationRequest loanApplicationRequest) {
        return loanApplicationService.update(id, loanApplicationRequest);
    }

    @DeleteMapping("/{id}")
    public LoanApplicationResponse delete(@PathVariable UUID id) {
        return loanApplicationService.delete(id);
    }
}

