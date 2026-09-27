package com.bagas.pinjam100.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FlowKirimService {

    private final WebClient flowKirimWebClient;
    private final ObjectMapper objectMapper;

    @Value("${flowkirim.api-token}")
    private String apiToken;

    @Value("${flowkirim.device-id}")
    private String deviceId;

    public String getSessionId() {

        String response = flowKirimWebClient.get()
                .uri(
                        "/api/whatsapp/sessions/{deviceId}",
                        deviceId
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiToken
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {

            JsonNode root =
                    objectMapper.readTree(response);

            if (!root.path("success").asBoolean()) {
                throw new IllegalStateException(
                        "Gagal mendapatkan session FlowKirim"
                );
            }

            return root.path("data")
                    .path("session_id")
                    .asText();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Gagal membaca response session FlowKirim",
                    e
            );
        }
    }

    public String sendOtp(
            String phoneNumber,
            String otpCode
    ) {

        String sessionId = getSessionId();

        String message = """
                *%s* adalah kode verifikasi Anda. Demi keamanan, jangan bagikan kode ini.
                """.formatted(otpCode);

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "session_id",
                sessionId
        );

        body.put(
                "to",
                phoneNumber
        );

        body.put(
                "message",
                message
        );

        return flowKirimWebClient.post()
                .uri("/api/whatsapp/messages/text")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiToken
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .accept(
                        MediaType.APPLICATION_JSON
                )
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}