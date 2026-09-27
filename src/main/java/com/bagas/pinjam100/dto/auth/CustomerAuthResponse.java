package com.bagas.pinjam100.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAuthResponse {

    private String token;
    private String refreshToken;
    private CustomerAuthUserResponse user;
    private long expiresAtMillis;
}