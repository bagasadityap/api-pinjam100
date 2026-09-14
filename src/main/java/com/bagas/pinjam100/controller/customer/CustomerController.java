package com.bagas.pinjam100.controller.customer;

import com.bagas.pinjam100.dto.request.customer.CustomerOnboardingRequest;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.service.customer.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> getAll() {
        return customerService.findAllByDeletedDateIsNull();
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable UUID id) {
        return customerService.findByIdAndDeletedDateIsNull(id);
    }

    @GetMapping("/{id}/detail")
    public CustomerDetailResponse getDetailById(@PathVariable UUID id) {
        return customerService.findDetailById(id);
    }

    @GetMapping("/pending")
    public List<CustomerResponse> getPendingCustomers() {
        return customerService.findPendingCustomer();
    }

    @GetMapping("/verified-limit-null")
    public List<CustomerResponse> getVerifiedAndLimitIsNull() {
        return customerService.findVerifiedAndLimitIsNull();
    }

    @PutMapping("/{id}")
    public CustomerResponse update(
            @PathVariable UUID id,
            @RequestBody CustomerRequest request
    ) {
        return customerService.update(id, request);
    }

    @PostMapping("/{id}/onboarding")
    public CustomerDetailResponse saveOnboarding(
            @PathVariable UUID id,
            @RequestBody CustomerOnboardingRequest request
    ) {
        return customerService.saveOnboarding(id, request);
    }

    @PutMapping("/{id}/onboarding")
    public CustomerDetailResponse updateOnboarding(
            @PathVariable UUID id,
            @RequestBody CustomerOnboardingRequest request
    ) {
        return customerService.updateOnboarding(id, request);
    }

    @PutMapping("/{id}/verify")
    public CustomerDetailResponse verify(
            @PathVariable UUID id,
            @RequestBody String verificationStatus
    ) {
        return customerService.verifyCustomer(
                id,
                VerificationStatus.valueOf(verificationStatus)
        );
    }

    @DeleteMapping("/{id}")
    public CustomerResponse delete(@PathVariable UUID id) {
        return customerService.delete(id);
    }
}