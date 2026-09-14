package com.bagas.pinjam100.repository.customer;

import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findByIdAndDeletedDateIsNull(UUID id);
    List<Document> findAllByCustomer_IdAndDeletedDateIsNull(UUID customerId);
}
