package com.bagas.pinjam100.entity.otp;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResendOtpRequest {
    @NotBlank
    private String phoneNumber;
}
