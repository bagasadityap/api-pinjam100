package com.bagas.pinjam100.controller.installment;

import com.bagas.pinjam100.dto.response.installment.LoanInstallmentResponse;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.service.installment.LoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/installment")
@RequiredArgsConstructor
public class LoanInstallmentController {

    private final LoanInstallmentService loanInstallmentService;

    @GetMapping("/{id}")
    public LoanInstallmentResponse getById(@PathVariable UUID id) {
        return loanInstallmentService.getById(id);
    }

    @GetMapping("/{loanApplicationId}/loan-application")
    public List<LoanInstallmentResponse> getByLoanApplicationId(
            @PathVariable UUID loanApplicationId
    ) {
        return loanInstallmentService.getByLoanApplication_Id(loanApplicationId);
    }

    @GetMapping("/{customerId}/customer")
    public List<LoanInstallmentResponse> getByCustomerId(
            @PathVariable UUID customerId
    ) {
        return loanInstallmentService.getByCustomerId(customerId);
    }

    @PostMapping("/{id}/pay")
    public LoanInstallmentResponse pay(@PathVariable UUID id) {
        return loanInstallmentService.pay(id);
    }
}