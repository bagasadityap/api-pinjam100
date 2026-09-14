package com.bagas.pinjam100.controller;

import com.bagas.pinjam100.dto.request.customer.DocumentRequest;
import com.bagas.pinjam100.dto.response.customer.DocumentResponse;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.service.customer.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponse save(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("customerId") UUID customerId
    ) {
        System.out.println("DOCUMENT CONTROLLER HIT");
        System.out.println("type = " + type);
        System.out.println("customerId = " + customerId);
        System.out.println("file = " + file.getOriginalFilename());

        DocumentRequest request = new DocumentRequest();
        request.setType(type);
        request.setCustomerId(customerId);

        return documentService.upload(file, request);
    }

    @PutMapping("/{id}/verify")
    public DocumentResponse verify(@PathVariable UUID id, @RequestBody String verificationStatus) {
        return documentService.verifyDocument(id, VerificationStatus.valueOf(verificationStatus));
    }

    @DeleteMapping("/{id}")
    public DocumentResponse delete(@PathVariable UUID id) {
        return documentService.delete(id);
    }
}