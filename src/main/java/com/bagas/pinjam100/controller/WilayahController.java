package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.service.WilayahService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wilayah")
@RequiredArgsConstructor
public class WilayahController {
    private final WilayahService wilayahService;

    @GetMapping("/provinces")
    public ResponseEntity<String> getProvinces() {
        return ResponseEntity.ok(
                wilayahService.getProvinces()
        );
    }

    @GetMapping("/regencies/{provinceCode}")
    public ResponseEntity<String> getRegencies(
            @PathVariable String provinceCode
    ) {
        return ResponseEntity.ok(
                wilayahService.getRegencies(provinceCode)
        );
    }
}