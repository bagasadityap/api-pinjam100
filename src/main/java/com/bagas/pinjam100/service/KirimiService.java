package com.bagas.pinjam100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KirimiService {

    private final WebClient kirimiWebClient;

    @Value("${kirimi.user-code}")
    private String userCode;

    @Value("${kirimi.secret}")
    private String secret;

    @Value("${kirimi.device-id}")
    private String deviceId;

    public String sendOtpMessage(String phoneNumber, String otpCode) {
        String message = """
                *%s* adalah kode verifikasi Anda. Demi keamanan, jangan bagikan kode ini.
                """.formatted(otpCode);

        Map<String, String> body = Map.of(
                "user_code", userCode,
                "secret", secret,
                "device_id", deviceId,
                "receiver", phoneNumber,
                "message", message
        );

        return kirimiWebClient.post()
                .uri("/v1/send-message-fast")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> new IllegalStateException(
                                        "Kirimi API error: " + response.statusCode() + " - " + errorBody
                                ))
                )
                .bodyToMono(String.class)
                .block();
    }
}