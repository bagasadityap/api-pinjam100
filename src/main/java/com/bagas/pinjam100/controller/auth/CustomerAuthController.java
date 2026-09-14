package com.bagas.pinjam100.controller.auth;

import com.bagas.pinjam100.dto.auth.CustomerAuthResponse;
import com.bagas.pinjam100.dto.auth.CustomerLoginRequest;
import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.CustomerRequest;
import com.bagas.pinjam100.dto.response.customer.CustomerResponse;
import com.bagas.pinjam100.entity.otp.ResendOtpRequest;
import com.bagas.pinjam100.entity.otp.VerifyOtpRequest;
import com.bagas.pinjam100.service.auth.CustomerAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth/customer", "/auth/customer/"})
public class CustomerAuthController {
    private final CustomerAuthService customerAuthService;

    public CustomerAuthController(CustomerAuthService customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> loginCustomer(
            @Valid @RequestBody CustomerLoginRequest request
    ) {
        return customerAuthService.login(request, "Login berhasil");
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> registerCustomer(
            @Valid @RequestBody CustomerRequest request
    ) {
        return customerAuthService.register(request);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<BaseResponse<CustomerAuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        return customerAuthService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<BaseResponse<Void>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request
    ) {
        return customerAuthService.resendOtp(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorization
    ) {
        String token = authorization.substring(7);
        return customerAuthService.logout(token);
    }
}