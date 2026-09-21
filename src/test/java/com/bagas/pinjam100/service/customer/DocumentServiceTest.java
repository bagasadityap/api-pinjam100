package com.bagas.pinjam100.service.customer;

import com.bagas.pinjam100.dto.request.customer.DocumentRequest;
import com.bagas.pinjam100.dto.response.customer.DocumentResponse;
import com.bagas.pinjam100.entity.customer.Customer;
import com.bagas.pinjam100.entity.customer.Document;
import com.bagas.pinjam100.entity.customer.VerificationStatus;
import com.bagas.pinjam100.repository.customer.CustomerRepository;
import com.bagas.pinjam100.repository.customer.DocumentRepository;
import com.bagas.pinjam100.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentServiceTest")
class DocumentServiceTest {

    private static final UUID DOCUMENT_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final String FILE_NAME = "ktp-customer.jpg";

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private DocumentService documentService;

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("should return document response when document exists")
        void shouldReturnDocumentResponseWhenFound() {
            Document document = createDocument();

            when(documentRepository.findById(DOCUMENT_ID))
                    .thenReturn(Optional.of(document));

            DocumentResponse result = documentService.findById(DOCUMENT_ID);

            assertNotNull(result);
            assertEquals(DOCUMENT_ID, result.getId());
            verify(documentRepository).findById(DOCUMENT_ID);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when document does not exist")
        void shouldThrowExceptionWhenDocumentNotFound() {
            when(documentRepository.findById(DOCUMENT_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> documentService.findById(DOCUMENT_ID)
            );

            assertEquals("Dokumen tidak ditemukan", exception.getMessage());
            verify(documentRepository).findById(DOCUMENT_ID);
        }
    }

    @Nested
    @DisplayName("upload")
    class UploadTest {

        @Test
        @DisplayName("should upload file and save document successfully")
        void shouldUploadDocumentSuccessfully() throws IOException {
            Customer customer = createCustomer();
            DocumentRequest request = createDocumentRequest();

            when(customerRepository.findById(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(fileStorageService.store(multipartFile))
                    .thenReturn(FILE_NAME);
            when(documentRepository.save(any(Document.class)))
                    .thenAnswer(invocation -> {
                        Document doc = invocation.getArgument(0);
                        doc.setId(DOCUMENT_ID);
                        return doc;
                    });

            DocumentResponse result = documentService.upload(multipartFile, request);

            assertNotNull(result);
            assertEquals(DOCUMENT_ID, result.getId());

            verify(customerRepository).findById(CUSTOMER_ID);
            verify(fileStorageService).store(multipartFile);
            verify(documentRepository).save(any(Document.class));
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when customer not found during upload")
        void shouldThrowExceptionWhenCustomerNotFound() {
            DocumentRequest request = createDocumentRequest();

            when(customerRepository.findById(CUSTOMER_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> documentService.upload(multipartFile, request)
            );

            assertEquals("Customer tidak ditemukan", exception.getMessage());
            verify(customerRepository).findById(CUSTOMER_ID);
            verifyNoInteractions(fileStorageService);
            verify(documentRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw RuntimeException when fileStorageService throws IOException")
        void shouldThrowRuntimeExceptionWhenStorageFails() throws IOException {
            Customer customer = createCustomer();
            DocumentRequest request = createDocumentRequest();

            when(customerRepository.findById(CUSTOMER_ID))
                    .thenReturn(Optional.of(customer));
            when(fileStorageService.store(multipartFile))
                    .thenThrow(new IOException("Storage error"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> documentService.upload(multipartFile, request)
            );

            assertEquals("Gagal menyimpan file", exception.getMessage());
            verify(documentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("verifyDocument")
    class VerifyDocumentTest {

        @Test
        @DisplayName("should update document verification status successfully")
        void shouldVerifyDocumentSuccessfully() {
            Document document = createDocument();

            when(documentRepository.findById(DOCUMENT_ID))
                    .thenReturn(Optional.of(document));
            when(documentRepository.save(any(Document.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            DocumentResponse result = documentService.verifyDocument(DOCUMENT_ID, VerificationStatus.VERIFIED);

            assertNotNull(result);
            assertEquals(VerificationStatus.VERIFIED, document.getVerificationStatus());
            verify(documentRepository).findById(DOCUMENT_ID);
            verify(documentRepository).save(document);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when document to verify not found")
        void shouldThrowExceptionWhenDocumentToVerifyNotFound() {
            when(documentRepository.findById(DOCUMENT_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> documentService.verifyDocument(DOCUMENT_ID, VerificationStatus.VERIFIED)
            );

            assertEquals("Dokumen tidak ditemukan", exception.getMessage());
            verify(documentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("should delete file from storage and remove document entity successfully")
        void shouldDeleteDocumentSuccessfully() throws IOException {
            Document document = createDocument();

            when(documentRepository.findById(DOCUMENT_ID))
                    .thenReturn(Optional.of(document));

            DocumentResponse result = documentService.delete(DOCUMENT_ID);

            assertNotNull(result);
            verify(fileStorageService).delete(FILE_NAME);
            verify(documentRepository).delete(document);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when document to delete not found")
        void shouldThrowExceptionWhenDocumentToDeleteNotFound() {
            when(documentRepository.findById(DOCUMENT_ID))
                    .thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> documentService.delete(DOCUMENT_ID)
            );

            assertEquals("Dokumen tidak ditemukan", exception.getMessage());
            verifyNoInteractions(fileStorageService);
            verify(documentRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should throw RuntimeException when fileStorageService fails on delete")
        void shouldThrowRuntimeExceptionWhenFileDeletionFails() throws IOException {
            Document document = createDocument();

            when(documentRepository.findById(DOCUMENT_ID))
                    .thenReturn(Optional.of(document));
            doThrow(new IOException("Deletion error"))
                    .when(fileStorageService).delete(FILE_NAME);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> documentService.delete(DOCUMENT_ID)
            );

            assertEquals("Gagal menghapus file dokumen", exception.getMessage());
            verify(documentRepository, never()).delete(any());
        }
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setFullName("Bagas Aditya");
        return customer;
    }

    private Document createDocument() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setCustomer(createCustomer());
        document.setType("KTP");
        document.setFileUrl(FILE_NAME);
        document.setVerificationStatus(VerificationStatus.PENDING);
        return document;
    }

    private DocumentRequest createDocumentRequest() {
        DocumentRequest request = new DocumentRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setType("KTP");
        return request;
    }
}