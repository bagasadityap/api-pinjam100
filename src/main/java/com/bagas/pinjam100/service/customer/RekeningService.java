package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.repository.customer.RekeningRepository;
import org.springframework.stereotype.Service;

@Service
public class RekeningService {
    private final RekeningRepository rekeningRepository;

    public RekeningService(RekeningRepository rekeningRepository) {
        this.rekeningRepository = rekeningRepository;
    }
}
