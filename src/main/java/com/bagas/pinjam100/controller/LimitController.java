package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.request.customer.LimitRequest;
import com.bagas.pinjam100.dto.response.customer.LimitResponse;
import com.bagas.pinjam100.service.customer.CustomerLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customer-limit")
@RequiredArgsConstructor
public class LimitController {
    private final CustomerLimitService customerLimitService;

    @GetMapping("{id}/customer")
    public LimitResponse findByCustomer_Id(@PathVariable UUID id) {
        return customerLimitService.findByCustomer_Id(id);
    }

    @PostMapping
    public LimitResponse save(@RequestBody LimitRequest limitRequest) {
        return customerLimitService.save(limitRequest);
    }
}
