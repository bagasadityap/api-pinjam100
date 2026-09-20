package com.bagas.pinjam100.controller.customer;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerOnboardingRequest;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerDetailResponse;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.service.customer.CustomerService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BaseResponse<List<CustomerResponse>>> getAll() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data customer berhasil ditemukan",
                        customerService.findAllByDeletedDateIsNull()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CustomerResponse>> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data customer berhasil ditemukan",
                        customerService.findByIdAndDeletedDateIsNull(id)
                )
        );
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<BaseResponse<CustomerDetailResponse>> getDetailById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Detail customer berhasil ditemukan",
                        customerService.findDetailById(id)
                )
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<BaseResponse<List<CustomerResponse>>> getPendingCustomers() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data customer pending berhasil ditemukan",
                        customerService.findPendingCustomer()
                )
        );
    }

    @GetMapping("/verified-limit-null")
    public ResponseEntity<BaseResponse<List<CustomerResponse>>> getVerifiedAndLimitIsNull() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data customer berhasil ditemukan",
                        customerService.findVerifiedAndLimitIsNull()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CustomerResponse>> update(
            @PathVariable UUID id,
            @RequestBody CustomerRequest request
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data customer berhasil diperbarui",
                        customerService.update(id, request)
                )
        );
    }

    @PostMapping("/{id}/onboarding")
    public ResponseEntity<BaseResponse<CustomerDetailResponse>> saveOnboarding(
            @PathVariable UUID id,
            @RequestBody CustomerOnboardingRequest request
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Onboarding customer berhasil disimpan",
                        customerService.saveOnboarding(id, request)
                )
        );
    }

    @PutMapping("/{id}/onboarding")
    public ResponseEntity<BaseResponse<CustomerDetailResponse>> updateOnboarding(
            @PathVariable UUID id,
            @RequestBody CustomerOnboardingRequest request
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data onboarding customer berhasil diperbarui",
                        customerService.updateOnboarding(id, request)
                )
        );
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<BaseResponse<CustomerDetailResponse>> verify(
            @PathVariable UUID id,
            @RequestBody String verificationStatus
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Status verifikasi customer berhasil diperbarui",
                        customerService.verifyCustomer(
                                id,
                                VerificationStatus.valueOf(verificationStatus)
                        )
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<CustomerResponse>> delete(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Customer berhasil dihapus",
                        customerService.delete(id)
                )
        );
    }
}