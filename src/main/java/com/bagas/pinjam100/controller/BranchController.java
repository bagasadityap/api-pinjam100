package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.request.BranchRequest;
import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.service.BranchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/branch")
public class  BranchController {
    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public List<BranchResponse> getAll() {
        return branchService.findAllByDeletedDateIsNull();
    }

    @GetMapping("/{id}")
    public BranchResponse getById(@PathVariable UUID id) {
        return branchService.findByIdAndDeletedDateIsNull(id);
    }

    @PostMapping
    public BranchResponse create(@RequestBody BranchRequest branchRequest) {
        return branchService.save(branchRequest);
    }

    @PutMapping("/{id}")
    public BranchResponse update(@PathVariable UUID id, @RequestBody BranchRequest branchRequest) {
        return branchService.update(id, branchRequest);
    }

    @DeleteMapping("/{id}")
    public BranchResponse delete(@PathVariable UUID id) {
        return branchService.delete(id);
    }
}

