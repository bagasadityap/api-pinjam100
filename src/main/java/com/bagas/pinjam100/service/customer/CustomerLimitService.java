package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.dto.request.customer.LimitRequest;
import com.bagas.pinjam100.dto.response.customer.LimitResponse;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLimitService {
    private final CustomerLimitRepository customerLimitRepository;
    private final CustomerRepository customerRepository;

    public List<LimitResponse> findAll() {
        return customerLimitRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(LimitResponse::new)
                .toList();
    }

    public LimitResponse findByIdAndDeletedDateIsNull(UUID id) {
        CustomerLimit limit = customerLimitRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Data limit tidak ditemukan"));
        return new LimitResponse(limit);
    }

    public LimitResponse findByCustomer_Id(UUID id) {
        CustomerLimit limit = customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Data limit tidak ditemukan"));
        return new LimitResponse(limit);
    }

    public LimitResponse save(LimitRequest request) {
        CustomerLimit limit = new CustomerLimit();
        limit.setCreditLimit(request.getCreditLimit());
        limit.setCustomer(customerRepository.findByIdAndDeletedDateIsNull(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Data limit tidak ditemukan")));
        customerLimitRepository.save(limit);

        return new LimitResponse(limit);
    }
}
