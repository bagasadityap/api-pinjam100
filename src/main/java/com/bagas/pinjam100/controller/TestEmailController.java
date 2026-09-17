//package com.bagas.pinjam100.controller;
//
//import com.bagas.pinjam100.service.email.EmailService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/test/email")
//@RequiredArgsConstructor
//public class TestEmailController {
//
//    private final EmailService emailService;
//
//    @PostMapping
//    public ResponseEntity<Void> sendTestEmail(
//            @RequestParam String email
//    ) {
//        emailService.sendPasswordResetEmail(email);
//
//        return ResponseEntity.ok().build();
//    }
//}
