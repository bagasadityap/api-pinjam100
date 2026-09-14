package com.bagas.pinjam100.repository.customer;

import com.bagas.pinjam100.entity.customer.CustomerLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerLimitRespository extends JpaRepository<CustomerLimit, UUID> {
    Optional<CustomerLimit> findByIdAndDeletedDateIsNull(UUID id);
    Optional<CustomerLimit> findByCustomer_IdAndDeletedDateIsNull(UUID customerId);
}
