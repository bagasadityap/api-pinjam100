package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.common.BaseResponse;
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
    public ResponseEntity<BaseResponse<String>> getProvinces() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data provinsi berhasil diambil",
                        wilayahService.getProvinces()
                )
        );
    }

    @GetMapping("/regencies/{provinceCode}")
    public ResponseEntity<BaseResponse<String>> getRegencies(
            @PathVariable String provinceCode
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data kabupaten/kota berhasil diambil",
                        wilayahService.getRegencies(provinceCode)
                )
        );
    }
}