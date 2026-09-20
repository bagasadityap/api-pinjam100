package com.bagas.pinjam100.repository.loanapplication;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.response.loanapplication.DisbursementResponse;
import com.bagas.pinjam100.service.loanapplication.LoanDisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/disbursement")
@RequiredArgsConstructor
public class LoanDisbursementController {

    private final LoanDisbursementService loanDisbursementService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<DisbursementResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data pencariran berhasil ditemukan",
                        loanDisbursementService.getById(id)
                )
        );
    }
}
