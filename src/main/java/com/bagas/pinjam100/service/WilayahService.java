package com.bagas.pinjam100.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WilayahService {

    public static final String CACHE_PROVINCES = "provinces";
    public static final String CACHE_REGENCIES = "regencies";

    private final RestClient restClient;

    public WilayahService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://wilayah.id/api")
                .build();
    }

    @Cacheable(cacheNames = CACHE_PROVINCES, key = "'all'")
    public String getProvinces() {
        return restClient.get()
                .uri("/provinces.json")
                .retrieve()
                .body(String.class);
    }

    @Cacheable(cacheNames = CACHE_REGENCIES, key = "#provinceCode")
    public String getRegencies(String provinceCode) {
        return restClient.get()
                .uri("/regencies/{code}.json", provinceCode)
                .retrieve()
                .body(String.class);
    }
}