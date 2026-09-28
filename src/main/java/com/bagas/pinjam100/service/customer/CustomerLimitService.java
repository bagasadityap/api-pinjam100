package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.config.CacheNames;
import com.bagas.pinjam100.dto.request.customer.LimitRequest;
import com.bagas.pinjam100.dto.response.customer.LimitResponse;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerLimit;
import com.bagas.pinjam100.repository.customer.CustomerLimitRepository;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLimitService {

    private final CustomerLimitRepository customerLimitRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;

    @Cacheable(cacheNames = CacheNames.CACHE_LIMIT_ALL, key = "'all_active'")
    public List<LimitResponse> findAll() {
        return customerLimitRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(LimitResponse::new)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.CACHE_LIMIT, key = "#id")
    public LimitResponse findByIdAndDeletedDateIsNull(UUID id) {
        CustomerLimit limit = customerLimitRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Data limit tidak ditemukan"));
        return new LimitResponse(limit);
    }

    @Cacheable(cacheNames = CacheNames.CACHE_LIMIT, key = "'customer_' + #id")
    public LimitResponse findByCustomer_Id(UUID id) {
        CustomerLimit limit = customerLimitRepository.findByCustomer_IdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Data limit tidak ditemukan"));
        return new LimitResponse(limit);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_LIMIT_ALL, key = "'all_active'"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER, key = "#request.customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_DETAIL, key = "#request.customerId"),
            @CacheEvict(cacheNames = CacheNames.CACHE_CUSTOMER_ALL, allEntries = true)
    })
    public LimitResponse save(LimitRequest request) {
        CustomerLimit limit = new CustomerLimit();
        limit.setCreditLimit(request.getCreditLimit());
        limit.setAvailableLimit(request.getCreditLimit());

        Customer customer = customerRepository.findByIdAndDeletedDateIsNull(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer tidak ditemukan"));

        limit.setCustomer(customer);
        customerLimitRepository.save(limit);

        notificationService.sendToCustomer(
                customer,
                "Akun Anda Berhasil Diverifikasi",
                "Akun Anda telah berhasil diverifikasi. Anda kini dapat mulai mengajukan pinjaman.",
                "verification",
                null
        );

        return new LimitResponse(limit);
    }
}