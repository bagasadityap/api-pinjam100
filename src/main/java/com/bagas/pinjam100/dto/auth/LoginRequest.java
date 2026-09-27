package com.bagas.pinjam100.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String identityNumber;
    private String password;
}
