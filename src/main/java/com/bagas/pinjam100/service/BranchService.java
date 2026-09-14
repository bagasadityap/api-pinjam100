package com.bagas.pinjam100.service;

import com.bagas.pinjam100.dto.request.BranchRequest;
import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.repository.BranchRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;

    public List<BranchResponse> findAll() {
        return branchRepository.findAll()
                .stream()
                .map(BranchResponse::new)
                .toList();
    }

    public List<BranchResponse> findAllByDeletedDateIsNull() {
        return branchRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(BranchResponse::new)
                .toList();
    }

    public BranchResponse findById(UUID id) {
        Branch response = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabang tidak ditemukan"));
        return new BranchResponse(response);
    }

    public BranchResponse findByIdAndDeletedDateIsNull(UUID id) {
        Branch response = branchRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabang tidak ditemukan"));
        return new BranchResponse(response);
    }

    public BranchResponse save(BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setProvince(request.getProvince());
        branch.setCity(request.getCity());
        branch.setPostalCode(request.getPostalCode());
        branchRepository.save(branch);

        return new BranchResponse(branch);
    }

    public BranchResponse update(UUID id, BranchRequest request) {
        Branch branch = branchRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabang tidak ditemukan"));

        branch.setName(request.getName());
        branch.setProvince(request.getProvince());
        branch.setCity(request.getCity());
        branch.setPostalCode(request.getPostalCode());
        branchRepository.save(branch);

        return new BranchResponse(branch);
    }

    public BranchResponse delete(UUID id) {
        Branch branch = branchRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabang tidak ditemukan"));

        branch.setDeletedDate(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
        branchRepository.save(branch);
        return new BranchResponse(branch);
    }
}

