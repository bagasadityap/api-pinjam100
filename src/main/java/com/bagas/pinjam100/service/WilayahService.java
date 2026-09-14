package com.bagas.pinjam100.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WilayahService {
    private final RestClient restClient;

    public WilayahService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://wilayah.id/api")
                .build();
    }

    public String getProvinces() {
        return restClient.get()
                .uri("/provinces.json")
                .retrieve()
                .body(String.class);
    }

    public String getRegencies(String provinceCode) {
        return restClient.get()
                .uri("/regencies/{code}.json", provinceCode)
                .retrieve()
                .body(String.class);
    }
}