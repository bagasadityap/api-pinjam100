package com.bagas.pinjam100.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FlowKirimServiceTest")
class FlowKirimServiceTest {

    private MockWebServer mockWebServer;
    private FlowKirimService flowKirimService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        ObjectMapper objectMapper = new ObjectMapper();

        flowKirimService = new FlowKirimService(webClient, objectMapper);

        org.springframework.test.util.ReflectionTestUtils.setField(flowKirimService, "apiToken", "test-token-123");
        org.springframework.test.util.ReflectionTestUtils.setField(flowKirimService, "deviceId", "device-999");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("getSessionId")
    class GetSessionIdTest {

        @Test
        @DisplayName("should return session id successfully when api responds with success true")
        void shouldReturnSessionIdSuccessfully() {
            String jsonResponse = """
                    {
                        "success": true,
                        "data": {
                            "session_id": "sess_abc123"
                        }
                    }
                    """;

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(jsonResponse));

            String sessionId = flowKirimService.getSessionId();

            assertNotNull(sessionId);
            assertEquals("sess_abc123", sessionId);
        }

        @Test
        @DisplayName("should throw IllegalStateException when api responds with success false")
        void shouldThrowExceptionWhenSuccessIsFalse() {
            String jsonResponse = """
                    {
                        "success": false,
                        "message": "Device offline"
                    }
                    """;

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(jsonResponse));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> flowKirimService.getSessionId()
            );

            assertEquals("Gagal membaca response session FlowKirim", exception.getMessage());

            assertNotNull(exception.getCause());
            assertEquals("Gagal mendapatkan session FlowKirim", exception.getCause().getMessage());
        }

        @Test
        @DisplayName("should throw IllegalStateException when response body is malformed json")
        void shouldThrowExceptionOnMalformedJson() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody("invalid-json"));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> flowKirimService.getSessionId()
            );

            assertEquals("Gagal membaca response session FlowKirim", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("sendOtp")
    class SendOtpTest {

        @Test
        @DisplayName("should send otp text message successfully after getting session")
        void shouldSendOtpSuccessfully() {
            String sessionResponse = """
                    {
                        "success": true,
                        "data": {
                            "session_id": "sess_abc123"
                        }
                    }
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(sessionResponse));

            String sendResponse = """
                    {
                        "success": true,
                        "message": "Message sent"
                    }
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(sendResponse));

            String response = flowKirimService.sendOtp("08123456789", "123456");

            assertNotNull(response);
            assertTrue(response.contains("Message sent"));
        }
    }
}