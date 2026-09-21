package com.bagas.pinjam100.controller.loanapplication;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.loanapplication.ApprovalRequest;
import com.bagas.pinjam100.dto.request.loanapplication.LoanApplicationRequest;
import com.bagas.pinjam100.dto.request.loanapplication.ReviewRequest;
import com.bagas.pinjam100.dto.response.loanapplication.*;
import com.bagas.pinjam100.service.loanapplication.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loan-application")
@Tag(
        name = "Loan Applications",
        description = "Loan application management, review, approval, and disbursement operations"
)
@SecurityRequirement(name = "bearerAuth")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @Operation(
            summary = "Get all loan applications",
            description = "Retrieve all active loan applications"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:read')")
    @GetMapping
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan berhasil ditemukan",
                        loanApplicationService.findAllByDeletedDateIsNull()
                )
        );
    }

    @Operation(
            summary = "Get applications for review",
            description = "Retrieve loan applications pending review"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:review')")
    @GetMapping("/review")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAllForReview() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan review berhasil ditemukan",
                        loanApplicationService.findAllForReview()
                )
        );
    }

    @Operation(
            summary = "Get applications for approval",
            description = "Retrieve loan applications pending approval"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Approval applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:approve')")
    @GetMapping("/approval")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAllForApproval() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan approval berhasil ditemukan",
                        loanApplicationService.findAllForApproval()
                )
        );
    }

    @Operation(
            summary = "Get applications for disbursement",
            description = "Retrieve loan applications pending disbursement"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Disbursement applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:disburse')")
    @GetMapping("/disbursement")
    public ResponseEntity<BaseResponse<List<LoanApplicationResponse>>> getAllForDisbursement() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pengajuan pencairan berhasil ditemukan",
                        loanApplicationService.findAllForDisbursement()
                )
        );
    }

    @Operation(
            summary = "Get loan application by ID",
            description = "Retrieve loan application details by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan application retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
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

    @Operation(
            summary = "Get review detail by ID",
            description = "Retrieve review detail of a loan application by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review detail retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:review')")
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

    @Operation(
            summary = "Review loan application",
            description = "Submit review result for a loan application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan application reviewed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid review request data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data review tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:review')")
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

    @Operation(
            summary = "Get approval detail by ID",
            description = "Retrieve approval detail of a loan application by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Approval detail retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:approve')")
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

    @Operation(
            summary = "Approve loan application",
            description = "Submit approval or rejection decision for a loan application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan application decision processed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid approval request data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data approval tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:approve')")
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

    @Operation(
            summary = "Get disbursement detail by ID",
            description = "Retrieve disbursement detail of a loan application by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Disbursement detail retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:disburse')")
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

    @Operation(
            summary = "Disburse loan application",
            description = "Execute funds disbursement for an approved loan application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan disbursed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Loan application not ready for disbursement",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan belum disetujui atau status tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:disburse')")
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

    @Operation(
            summary = "Get loan applications by branch ID",
            description = "Retrieve all loan applications associated with a specific branch"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Branch loan applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Branch not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Branch tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:read')")
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

    @Operation(
            summary = "Get loan applications by customer ID",
            description = "Retrieve all loan applications for a specific customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer loan applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Customer tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
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

    @Operation(
            summary = "Create loan application",
            description = "Create a new loan application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Loan application created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid loan application request data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Data pengajuan tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Active loan application already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Masih terdapat pengajuan pinjaman yang sedang berjalan\",\"error\":\"Conflict\",\"status\":409,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<BaseResponse<LoanApplicationResponse>> create(
            @RequestBody LoanApplicationRequest loanApplicationRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(
                        HttpStatus.CREATED,
                        "Pengajuan berhasil dibuat",
                        loanApplicationService.save(loanApplicationRequest)
                )
        );
    }

    @Operation(
            summary = "Delete loan application",
            description = "Delete a loan application by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan application deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan application not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Pengajuan pinjaman tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('loan:delete')")
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