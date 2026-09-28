package com.bagas.pinjam100.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KirimiServiceTest")
class KirimiServiceTest {

    private MockWebServer mockWebServer;
    private KirimiService kirimiService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Inisialisasi MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // 2. Buat WebClient yang mengarah ke URL MockWebServer
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        // 3. Inisialisasi Service
        kirimiService = new KirimiService(webClient);

        // 4. Inject nilai @Value menggunakan ReflectionTestUtils
        ReflectionTestUtils.setField(kirimiService, "userCode", "KM-TEST");
        ReflectionTestUtils.setField(kirimiService, "secret", "secret123");
        ReflectionTestUtils.setField(kirimiService, "deviceId", "DEV-01");

        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("should send OTP message successfully and return response body")
    void shouldSendOtpMessageSuccessfully() throws Exception {
        // Arrange
        String phoneNumber = "628123456789";
        String otpCode = "123456";
        String expectedResponseBody = "{\"status\":\"success\",\"message\":\"Message sent\"}";

        // Siapkan response palsu dari server (HTTP 200 OK)
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(expectedResponseBody));

        // Act
        String actualResponse = kirimiService.sendOtpMessage(phoneNumber, otpCode);

        // Assert Response
        assertEquals(expectedResponseBody, actualResponse);

        // Assert Request yang dikirim oleh WebClient
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/v1/send-message-fast", recordedRequest.getPath());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));

        // Verifikasi Body Request
        String requestBodyString = recordedRequest.getBody().readUtf8();
        Map<String, String> requestBody = objectMapper.readValue(requestBodyString, Map.class);

        assertEquals("KM-TEST", requestBody.get("user_code"));
        assertEquals("secret123", requestBody.get("secret"));
        assertEquals("DEV-01", requestBody.get("device_id"));
        assertEquals(phoneNumber, requestBody.get("receiver"));
        assertTrue(requestBody.get("message").contains("*123456* adalah kode verifikasi Anda"));
    }

    @Test
    @DisplayName("should throw IllegalStateException when API returns error (4xx/5xx)")
    void shouldThrowExceptionWhenApiReturnsError() {
        // Arrange
        String phoneNumber = "628123456789";
        String otpCode = "123456";
        String errorResponseBody = "{\"message\":\"The route v1/send-message-fast could not be found.\"}";

        // Siapkan response error dari server (HTTP 404 Not Found)
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorResponseBody));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> kirimiService.sendOtpMessage(phoneNumber, otpCode)
        );

        assertTrue(exception.getMessage().contains("Kirimi API error: 404 NOT_FOUND"));
        assertTrue(exception.getMessage().contains(errorResponseBody));
    }
}
