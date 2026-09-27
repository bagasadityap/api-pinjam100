package com.bagas.pinjam100.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Password saat ini tidak boleh kosong")
    private String currentPassword;

    @NotBlank(message = "Password baru tidak boleh kosong")
    private String newPassword;
}
