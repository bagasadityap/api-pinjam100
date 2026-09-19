package com.bagas.pinjam100.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequest {

    @NotBlank(message = "Refresh token wajib diisi")
    private String refreshToken;
}