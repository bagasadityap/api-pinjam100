package com.bagas.pinjam100.controller.installment;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.response.installment.LoanInstallmentResponse;
import com.bagas.pinjam100.service.installment.LoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/installment")
@RequiredArgsConstructor
public class LoanInstallmentController {

    private final LoanInstallmentService loanInstallmentService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LoanInstallmentResponse>> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data angsuran berhasil ditemukan",
                        loanInstallmentService.getById(id)
                )
        );
    }

    @GetMapping("/{loanApplicationId}/loan-application")
    public ResponseEntity<BaseResponse<List<LoanInstallmentResponse>>> getByLoanApplicationId(
            @PathVariable UUID loanApplicationId
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data angsuran berhasil ditemukan",
                        loanInstallmentService.getByLoanApplication_Id(loanApplicationId)
                )
        );
    }

    @GetMapping("/{customerId}/customer")
    public ResponseEntity<BaseResponse<List<LoanInstallmentResponse>>> getByCustomerId(
            @PathVariable UUID customerId
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data angsuran berhasil ditemukan",
                        loanInstallmentService.getByCustomerId(customerId)
                )
        );
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<BaseResponse<LoanInstallmentResponse>> pay(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Angsuran berhasil dibayar",
                        loanInstallmentService.pay(id)
                )
        );
    }
}