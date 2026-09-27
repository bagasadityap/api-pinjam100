package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.config.CacheNames;
import com.bagas.pinjam100.dto.request.customer.DocumentRequest;
import com.bagas.pinjam100.dto.response.customer.DocumentResponse;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.Document;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.customer.DocumentRepository;
import com.bagas.pinjam100.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;

    DocumentService(DocumentRepository documentRepository, CustomerRepository customerRepository, FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.customerRepository = customerRepository;
        this.fileStorageService = fileStorageService;
    }

    @Cacheable(cacheNames = CacheNames.CACHE_DOCUMENT, key = "#id")
    public DocumentResponse findById(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dokumen tidak ditemukan"));
        return new DocumentResponse(document);
    }

    @CacheEvict(cacheNames = CacheNames.CACHE_DOCUMENT_ALL, key = "'all'")
    public DocumentResponse upload(MultipartFile file, DocumentRequest documentRequest) {
        Customer customer = customerRepository.findById(documentRequest.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer tidak ditemukan"));

        try {
            String fileName = fileStorageService.store(file);

            Document document = new Document();
            document.setCustomer(customer);
            document.setType(documentRequest.getType());
            document.setFileUrl(fileName);
            document.setVerificationStatus(VerificationStatus.PENDING);

            documentRepository.save(document);

            return new DocumentResponse(document);
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file", e);
        }
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_DOCUMENT, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_DOCUMENT_ALL, key = "'all'")
    })
    public DocumentResponse verifyDocument(UUID id, VerificationStatus verificationStatus) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dokumen tidak ditemukan"));

        document.setVerificationStatus(verificationStatus);
        documentRepository.save(document);

        return new DocumentResponse(document);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CACHE_DOCUMENT, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.CACHE_DOCUMENT_ALL, key = "'all'")
    })
    public DocumentResponse delete(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dokumen tidak ditemukan"));

        try {
            fileStorageService.delete(document.getFileUrl());
        } catch (IOException e) {
            throw new RuntimeException("Gagal menghapus file dokumen", e);
        }

        documentRepository.delete(document);
        return new DocumentResponse(document);
    }
}