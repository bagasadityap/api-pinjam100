package com.bagas.pinjam100.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AuthResponse {

    private String token;
    private String identityNumber;
    private String role;
    private List<String> permissions;

    public AuthResponse() {
    }

    public AuthResponse(
            String token,
            String identityNumber,
            String role,
            List<String> permissions
    ) {
        this.token = token;
        this.identityNumber = identityNumber;
        this.role = role;
        this.permissions = permissions;
    }
}