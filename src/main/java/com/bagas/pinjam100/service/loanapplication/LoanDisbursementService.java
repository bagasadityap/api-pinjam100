package com.bagas.pinjam100.service.loanapplication;

import com.bagas.pinjam100.dto.response.loanapplication.DisbursementResponse;
import com.bagas.pinjam100.entity.loanapplication.LoanDisbursement;
import com.bagas.pinjam100.repository.loanapplication.LoanApplicationDisbursementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LoanDisbursementService {

    public static final String CACHE_DISBURSEMENT = "loan_disbursement";

    private final LoanApplicationDisbursementRepository loanApplicationDisbursementRepository;

    @Cacheable(cacheNames = CACHE_DISBURSEMENT, key = "#disbursementId")
    public DisbursementResponse getById(UUID disbursementId) {
        LoanDisbursement disbursement = loanApplicationDisbursementRepository.findById(disbursementId)
                .orElseThrow(() -> new EntityNotFoundException("Disbursement data tidak ditemukan"));
        return new DisbursementResponse(disbursement);
    }
}