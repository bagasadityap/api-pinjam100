package com.bagas.pinjam100.service.installment;

import com.bagas.pinjam100.config.CacheNames;
import com.bagas.pinjam100.dto.response.installment.LoanInstallmentResponse;
import com.bagas.pinjam100.entity.installment.InstallmentStatus;
import com.bagas.pinjam100.entity.installment.LoanInstallment;
import com.bagas.pinjam100.repository.installment.LoanInstallmentRepository;
import com.bagas.pinjam100.service.dashboard.DashboardService;
import com.bagas.pinjam100.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(cacheNames = CacheNames.CACHE_INSTALLMENT, key = "#id")
    public LoanInstallmentResponse getById(UUID id) {
        LoanInstallment installment = loanInstallmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Angsuran tidak ditemukan"));

        return new LoanInstallmentResponse(installment);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.CACHE_INSTALLMENT_APPLICATION, key = "#loanApplicationId")
    public List<LoanInstallmentResponse> getByLoanApplication_Id(UUID loanApplicationId) {
        return loanInstallmentRepository.findByLoanApplication_Id(loanApplicationId)
                .stream()
                .map(LoanInstallmentResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.CACHE_INSTALLMENT_CUSTOMER, key = "#customerId")
    public List<LoanInstallmentResponse> getByCustomerId(UUID customerId) {
        return loanInstallmentRepository.findByLoanApplication_Customer_Id(customerId)
                .stream()
                .map(LoanInstallmentResponse::new)
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_INSTALLMENT, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_INSTALLMENT_APPLICATION, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.CACHE_INSTALLMENT_CUSTOMER, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.CACHE_DASHBOARD, allEntries = true)
    })
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