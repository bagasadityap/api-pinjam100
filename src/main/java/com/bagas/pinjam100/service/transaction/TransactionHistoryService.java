package com.bagas.pinjam100.service.transaction;

import com.bagas.pinjam100.dto.response.transaction.TransactionHistoryResponse;
import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import com.bagas.pinjam100.repository.installment.LoanInstallmentRepository;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationDisbursementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionHistoryService {

    public static final String CACHE_TRANSACTION_HISTORY = "transaction_history";

    private final LoanApplicationDisbursementRepository loanApplicationDisbursementRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    @Transactional
    @Cacheable(cacheNames = CACHE_TRANSACTION_HISTORY, key = "#customerId")
    public List<TransactionHistoryResponse> getByCustomerId(UUID customerId) {

        List<TransactionHistoryResponse> transactions = new ArrayList<>();

        loanApplicationDisbursementRepository
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