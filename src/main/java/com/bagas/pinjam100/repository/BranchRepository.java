package com.bagas.pinjam100.repository;

import com.bagas.pinjam100.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByNameAndDeletedDateIsNull(String name);
    Optional<Branch> findByCityAndDeletedDateIsNull(String city);
    Optional<Branch> findByIdAndDeletedDateIsNull(UUID id);
    List<Branch> findAllByDeletedDateIsNull();
}
