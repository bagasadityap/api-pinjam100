package com.bagas.pinjam100.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("WilayahServiceTest")
class WilayahServiceTest {

    private WilayahService wilayahService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://wilayah.id/api");
        mockServer = MockRestServiceServer.bindTo(builder).build();

        wilayahService = new WilayahService();

        org.springframework.test.util.ReflectionTestUtils.setField(
                wilayahService,
                "restClient",
                builder.build()
        );
    }

    @Nested
    @DisplayName("getProvinces")
    class GetProvincesTest {

        @Test
        @DisplayName("should return json string of provinces successfully")
        void shouldReturnProvincesSuccessfully() {
            String mockJsonResponse = "{\"data\":[{\"code\":\"31\",\"name\":\"DKI JAKARTA\"}]}";

            mockServer.expect(requestTo("https://wilayah.id/api/provinces.json"))
                    .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

            String result = wilayahService.getProvinces();

            assertNotNull(result);
            assertEquals(mockJsonResponse, result);
            mockServer.verify();
        }
    }

    @Nested
    @DisplayName("getRegencies")
    class GetRegenciesTest {

        @Test
        @DisplayName("should return json string of regencies for given province code successfully")
        void shouldReturnRegenciesSuccessfully() {
            String provinceCode = "31";
            String mockJsonResponse = "{\"data\":[{\"code\":\"3171\",\"name\":\"KOTA JAKARTA SELATAN\"}]}";

            mockServer.expect(requestTo("https://wilayah.id/api/regencies/31.json"))
                    .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

            String result = wilayahService.getRegencies(provinceCode);

            assertNotNull(result);
            assertEquals(mockJsonResponse, result);
            mockServer.verify();
        }
    }
}
