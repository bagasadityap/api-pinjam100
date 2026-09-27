package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.service.WilayahService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wilayah")
@RequiredArgsConstructor
@Tag(
        name = "Wilayah",
        description = "Regional and territory data operations"
)
@SecurityRequirement(name = "bearerAuth")
public class WilayahController {

    private final WilayahService wilayahService;

    @Operation(
            summary = "Get provinces",
            description = "Retrieve list of all provinces"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Province data retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @GetMapping("/provinces")
    public ResponseEntity<BaseResponse<String>> getProvinces() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data provinsi berhasil diambil",
                        wilayahService.getProvinces()
                )
        );
    }

    @Operation(
            summary = "Get regencies by province code",
            description = "Retrieve list of regencies/cities by province code"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Regency data retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Silakan melakukan login\",\"error\":\"Unauthorized\",\"status\":401,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Anda tidak memiliki akses ke resource ini\",\"error\":\"Forbidden\",\"status\":403,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Province code not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Kode provinsi tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
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