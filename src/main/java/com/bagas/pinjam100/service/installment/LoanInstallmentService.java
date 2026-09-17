package com.bagas.pinjam100.service.installment;

import com.bagas.pinjam100.dto.response.installment.LoanInstallmentResponse;
import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.repository.installment.LoanInstallmentRepository;
import com.bagas.pinjam100.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanInstallmentService {

    private final LoanInstallmentRepository loanInstallmentRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public LoanInstallmentResponse getById(UUID id) {
        LoanInstallment installment = loanInstallmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Angsuran tidak ditemukan"));

        return new LoanInstallmentResponse(installment);
    }

    @Transactional(readOnly = true)
    public List<LoanInstallmentResponse> getByLoanApplication_Id(UUID loanApplicationId) {
        return loanInstallmentRepository.findByLoanApplication_Id(loanApplicationId)
                .stream()
                .map(LoanInstallmentResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanInstallmentResponse> getByCustomerId(UUID customerId) {
        return loanInstallmentRepository.findByLoanApplication_Customer_Id(customerId)
                .stream()
                .map(LoanInstallmentResponse::new)
                .toList();
    }

    public LoanInstallmentResponse pay(UUID id) {
        LoanInstallment loanInstallment = loanInstallmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Angsuran tidak ditemukan"));

        loanInstallment.setPaidAmount(loanInstallment.getInstallmentAmount());
        loanInstallment.setStatus(InstallmentStatus.PAID);
        loanInstallment.setPaidDate(LocalDateTime.now());

        loanInstallmentRepository.save(loanInstallment);

        notificationService.sendToCustomer(
                loanInstallment.getLoanApplication().getCustomer(),
                "Pembayaran Angsuran Berhasil",
                "Pembayaran angsuran Anda telah berhasil diterima. Terima kasih atas pembayaran yang telah dilakukan.",
                "installment",
                "pinjam100://installment/" + loanInstallment.getId()
        );

        return new LoanInstallmentResponse(loanInstallment);
    }
}