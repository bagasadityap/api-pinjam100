package com.bagas.pinjam100.controller.loanapplication;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.loanapplication.ApprovalRequest;
import com.bagas.pinjam100.dto.request.loanapplication.DisbursementRequest;
import com.bagas.pinjam100.dto.request.loanapplication.LoanApplicationRequest;
import com.bagas.pinjam100.dto.request.loanapplication.ReviewRequest;
import com.bagas.pinjam100.dto.response.loanapplication.*;
import com.bagas.pinjam100.service.loanapplication.LoanApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loan-application")
public class LoanApplicationController {
    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan berhasil ditemukan",
                        loanApplicationService.findAllByDeletedDateIsNull()
                )
        );
    }

    @GetMapping("/review")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAllForReview() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan review berhasil ditemukan",
                        loanApplicationService.findAllForReview()
                )
        );
    }

    @GetMapping("/approval")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAllForApproval() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan approval berhasil ditemukan",
                        loanApplicationService.findAllForApproval()
                )
        );
    }

    @GetMapping("/disbursement")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAllForDisbursement() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan pencairan berhasil ditemukan",
                        loanApplicationService.findAllForDisbursement()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LoanApplicationResponse>> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan berhasil ditemukan",
                        loanApplicationService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<BaseResponse<LoanApplicationReviewResponse>> getByIdReview(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Detail review pengajuan berhasil ditemukan",
                        loanApplicationService.findByIdForReview(id)
                )
        );
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<BaseResponse<LoanApplicationResponse>> review(
            @PathVariable UUID id,
            @RequestBody ReviewRequest reviewRequest
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Pengajuan berhasil direview",
                        loanApplicationService.review(id, reviewRequest)
                )
        );
    }

    @GetMapping("/{id}/approval")
    public ResponseEntity<BaseResponse<LoanApplicationApprovalResponse>> getByIdApproval(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Detail approval pengajuan berhasil ditemukan",
                        loanApplicationService.findByIdForApproval(id)
                )
        );
    }

    @PostMapping("/{id}/approval")
    public ResponseEntity<BaseResponse<LoanApplicationResponse>> approve(
            @PathVariable UUID id,
            @RequestBody ApprovalRequest approvalRequest
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Pengajuan berhasil disetujui",
                        loanApplicationService.approve(id, approvalRequest)
                )
        );
    }

    @GetMapping("/{id}/disbursement")
    public ResponseEntity<BaseResponse<LoanApplicationDisbursementResponse>> getByIdDisbursement(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Detail pencairan pengajuan berhasil ditemukan",
                        loanApplicationService.findByIdForDisbursement(id)
                )
        );
    }

    @PostMapping("/{id}/disbursement")
    public ResponseEntity<BaseResponse<LoanApplicationResponse>> disburse(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Pengajuan berhasil dicairkan",
                        loanApplicationService.disburse(id)
                )
        );
    }

    @GetMapping("/{id}/branch")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getByBranch(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan berdasarkan cabang berhasil ditemukan",
                        loanApplicationService.findByBranchAndDeletedDateIsNull(id)
                )
        );
    }

    @GetMapping("/{id}/customer")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getByCustomer(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan berdasarkan customer berhasil ditemukan",
                        loanApplicationService.findByCustomerAndDeletedDateIsNull(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<LoanApplicationResponse>> create(
            @RequestBody LoanApplicationRequest loanApplicationRequest
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Pengajuan berhasil dibuat",
                        loanApplicationService.save(loanApplicationRequest)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<LoanApplicationResponse>> delete(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Pengajuan berhasil dihapus",
                        loanApplicationService.delete(id)
                )
        );
    }
}