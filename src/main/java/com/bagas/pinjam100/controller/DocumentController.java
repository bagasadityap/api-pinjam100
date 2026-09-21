package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.dto.request.customer.DocumentRequest;
import com.bagas.pinjam100.dto.response.customer.DocumentResponse;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.service.customer.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
@Tag(
        name = "Documents",
        description = "Document management operations"
)
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(
            summary = "Upload document",
            description = "Upload a new document file for a customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Document uploaded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or parameters",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Format file atau data request tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
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
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Customer tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<DocumentResponse>> save(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("customerId") UUID customerId
    ) {
        DocumentRequest request = new DocumentRequest();
        request.setType(type);
        request.setCustomerId(customerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.success(
                        HttpStatus.CREATED,
                        "Dokumen berhasil diunggah",
                        documentService.upload(file, request)
                )
        );
    }

    @Operation(
            summary = "Verify document",
            description = "Update the verification status of a document"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Document status verified successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid verification status value",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Status verifikasi tidak valid\",\"error\":\"Bad Request\",\"status\":400,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
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
                    description = "Document not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Dokumen tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAuthority('document:verify')")
    @PutMapping("/{id}/verify")
    public ResponseEntity<BaseResponse<DocumentResponse>> verify(
            @PathVariable UUID id,
            @RequestBody String verificationStatus
    ) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Dokumen berhasil diverifikasi",
                        documentService.verifyDocument(id, VerificationStatus.valueOf(verificationStatus))
                )
        );
    }

    @Operation(
            summary = "Delete document",
            description = "Delete a document by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Document deleted successfully"
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
                    description = "Document not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Dokumen tidak ditemukan\",\"error\":\"Not Found\",\"status\":404,\"timestamp\":\"2026-09-21T03:29:45.746Z\"}"
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<DocumentResponse>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Dokumen berhasil dihapus",
                        documentService.delete(id)
                )
        );
    }
}