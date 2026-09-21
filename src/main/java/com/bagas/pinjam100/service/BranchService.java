package com.bagas.pinjam100.service;

import com.bagas.pinjam100.dto.request.BranchRequest;
import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.entity.Branch;
import com.bagas.pinjam100.repository.BranchRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class BranchService {

    public static final String CACHE_BRANCH = "branch";
    public static final String CACHE_BRANCH_ALL = "branch_all";

    private final BranchRepository branchRepository;

    @Cacheable(cacheNames = CACHE_BRANCH_ALL, key = "'all_active'")
    public List<BranchResponse> findAllByDeletedDateIsNull() {
        return branchRepository.findAllByDeletedDateIsNull()
                .stream()
                .map(BranchResponse::new)
                .toList();
    }

    @Cacheable(cacheNames = CACHE_BRANCH, key = "#id")
    public BranchResponse findByIdAndDeletedDateIsNull(UUID id) {
        Branch response = branchRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabang tidak ditemukan"));
        return new BranchResponse(response);
    }

    @CacheEvict(cacheNames = CACHE_BRANCH_ALL, key = "'all_active'")
    public BranchResponse save(BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setProvince(request.getProvince());
        branch.setCity(request.getCity());
        branch.setPostalCode(request.getPostalCode());
        branchRepository.save(branch);

        return new BranchResponse(branch);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_BRANCH, key = "#id"),
            @CacheEvict(cacheNames = CACHE_BRANCH_ALL, key = "'all_active'")
    })
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

    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_BRANCH, key = "#id"),
            @CacheEvict(cacheNames = CACHE_BRANCH_ALL, key = "'all_active'")
    })
    public BranchResponse delete(UUID id) {
        Branch branch = branchRepository.findByIdAndDeletedDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabang tidak ditemukan"));

        branch.setDeletedDate(LocalDateTime.now(ZoneId.of("Asia/Jakarta")));
        branchRepository.save(branch);
        return new BranchResponse(branch);
    }
}