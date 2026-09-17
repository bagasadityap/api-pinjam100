package com.bagas.pinjam100.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceRequest(
        @NotBlank String fcmToken
) {
}