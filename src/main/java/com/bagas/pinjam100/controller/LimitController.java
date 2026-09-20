package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.LimitRequest;
import com.bagas.pinjam100.dto.response.customer.LimitResponse;
import com.bagas.pinjam100.service.customer.CustomerLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customer-limit")
@RequiredArgsConstructor
public class LimitController {
    private final CustomerLimitService customerLimitService;

    @GetMapping("{id}/customer")
    public ResponseEntity<BaseResponse<LimitResponse>> findByCustomer_Id(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data limit customer berhasil diambil",
                        customerLimitService.findByCustomer_Id(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<LimitResponse>> save(@RequestBody LimitRequest limitRequest) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Limit customer berhasil disimpan",
                        customerLimitService.save(limitRequest)
                )
        );
    }
}