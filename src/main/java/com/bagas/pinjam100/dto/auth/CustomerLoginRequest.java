package com.bagas.pinjam100.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CustomerLoginRequest {
    private String phoneNumber;
    private String password;
    private String fcmToken;
}
