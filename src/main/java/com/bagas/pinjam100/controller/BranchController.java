package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.BranchRequest;
import com.bagas.pinjam100.dto.response.BranchResponse;
import com.bagas.pinjam100.service.BranchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/branch")
public class BranchController {
    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<BranchResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data branch berhasil diambil",
                        branchService.findAllByDeletedDateIsNull()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<BranchResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data branch berhasil diambil",
                        branchService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<BranchResponse>> create(@RequestBody BranchRequest branchRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Branch berhasil dibuat",
                        branchService.save(branchRequest)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<BranchResponse>> update(@PathVariable UUID id, @RequestBody BranchRequest branchRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Branch berhasil diperbarui",
                        branchService.update(id, branchRequest)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<BranchResponse>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Branch berhasil dihapus",
                        branchService.delete(id)
                )
        );
    }
}