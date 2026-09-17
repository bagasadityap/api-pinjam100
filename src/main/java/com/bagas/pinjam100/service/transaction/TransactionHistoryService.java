package com.bagas.pinjam100.service.transaction;

import com.bagas.pinjam100.dto.response.transaction.TransactionHistoryResponse;
import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import com.bagas.pinjam100.repository.installment.LoanInstallmentRepository;
import com.bagas.pinjam100.service.loanapplication.LoanDisbursementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionHistoryService {
    private final LoanDisbursementRepository loanDisbursementRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    public List<TransactionHistoryResponse> getByCustomerId(UUID customerId) {

        List<TransactionHistoryResponse> transactions = new ArrayList<>();

        loanDisbursementRepository
                .findByLoanApplication_Customer_Id(customerId)
                .stream()
                .map(this::mapDisbursement)
                .forEach(transactions::add);

        loanInstallmentRepository
                .findByLoanApplication_Customer_Id(customerId)
                .stream()
                .filter(installment ->
                        installment.getStatus() ==
                                InstallmentStatus.PAID
                )
                .map(this::mapPayment)
                .forEach(transactions::add);

        return transactions.stream()
                .sorted(Comparator.comparing(
                        TransactionHistoryResponse::getDate
                ).reversed())
                .toList();
    }

    private TransactionHistoryResponse mapDisbursement(
            LoanDisbursement disbursement
    ) {
        return new TransactionHistoryResponse(
                disbursement.getId(),
                TransactionType.DISBURSEMENT,
                disbursement.getTransactionReference(),
                disbursement.getDisbursementAmount(),
                disbursement.getCreatedDate(),
                TransactionStatus.SUCCESS
        );
    }

    private TransactionHistoryResponse mapPayment(
            LoanInstallment installment
    ) {
        return new TransactionHistoryResponse(
                installment.getId(),
                TransactionType.INSTALLMENT_PAYMENT,
                installment.getInstallmentNumber(),
                installment.getPaidAmount(),
                installment.getPaidDate(),
                TransactionStatus.SUCCESS
        );
    }
}
