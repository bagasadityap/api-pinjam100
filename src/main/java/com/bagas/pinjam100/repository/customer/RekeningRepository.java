package com.bagas.pinjam100.repository.customer;

import com.bagas.pinjam100.entity.customer.Rekening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RekeningRepository extends JpaRepository<Rekening, UUID> {
    Optional<Rekening> findByIdAndDeletedDateIsNull(UUID id);
    Optional<Rekening> findByCustomer_IdAndDeletedDateIsNull(UUID customerId);
    Optional<Rekening> findByIdAndCustomer_IdAndDeletedDateIsNull(UUID id, UUID customerId);
    List<Rekening> findAllByCustomer_IdAndDeletedDateIsNull(UUID customerId);
    List<Rekening> findAllByDeletedDateIsNull();
}
