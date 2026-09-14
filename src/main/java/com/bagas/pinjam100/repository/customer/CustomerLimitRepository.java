package com.bagas.pinjam100.repository.customer;

import com.bagas.pinjam100.entity.customer.CustomerLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerLimitRepository extends JpaRepository<CustomerLimit, UUID> {
    List<CustomerLimit> findAllByDeletedDateIsNull();
    Optional<CustomerLimit> findByIdAndDeletedDateIsNull(UUID id);
    Optional<CustomerLimit> findByCustomer_IdAndDeletedDateIsNull(UUID customerId);
}
