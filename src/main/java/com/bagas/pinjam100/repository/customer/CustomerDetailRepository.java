package com.bagas.pinjam100.repository.customer;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.CustomerDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerDetailRepository extends JpaRepository<CustomerDetail, UUID> {
    Optional<CustomerDetail> findByCustomer_IdAndDeletedDateIsNull(UUID customerId);
    boolean existsByNationalIdAndDeletedDateIsNull(UUID customerId);
}
