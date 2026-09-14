package com.bagas.pinjam100.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomerAuthUserResponse {
    private UUID id;
    private String customerNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean profileCompleted;
}