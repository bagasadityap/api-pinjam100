package com.bagas.pinjam100.repository.customer;

import com.bagas.pinjam100.entity.customer.CustomerEmployment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerEmploymentRepository extends JpaRepository<CustomerEmployment, UUID> {
    Optional<CustomerEmployment> findByIdAndDeletedDateIsNull(UUID id);
    List<CustomerEmployment> findAllByDeletedDateIsNull();
    Optional<CustomerEmployment> findByCustomer_IdAndDeletedDateIsNull(UUID customerId);
}

