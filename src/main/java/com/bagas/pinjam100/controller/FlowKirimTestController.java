package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.service.FlowKirimService;
import com.bagas.pinjam100.service.otp.OtpVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/flowkirim")
@RequiredArgsConstructor
public class FlowKirimTestController {

    private final FlowKirimService flowKirimService;
    private final OtpVerificationService otpVerificationService;

    @GetMapping("/session")
    public ResponseEntity<String> getSession() {
        return ResponseEntity.ok(
                flowKirimService.getSessionId()
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String phoneNumber
    ) {
        otpVerificationService.generate(phoneNumber);

        return ResponseEntity.ok("OTP berhasil dikirim");
    }
}
