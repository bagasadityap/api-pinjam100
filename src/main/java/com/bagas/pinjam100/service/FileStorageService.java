package com.bagas.pinjam100.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path storageDirectory;

    public FileStorageService(
            @Value("${app.storage.file-dir}") String directory
    ) {
        this.storageDirectory = Paths.get(directory)
                .toAbsolutePath()
                .normalize();
    }

    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File kosong");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Ukuran file maksimal 5 MB");
        }

        String extension = getExtension(file.getContentType());

        Files.createDirectories(storageDirectory);

        String fileName = UUID.randomUUID() + "." + extension;
        Path target = storageDirectory.resolve(fileName).normalize();

        if (!target.getParent().equals(storageDirectory)) {
            throw new IllegalArgumentException("Nama file tidak valid");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return fileName;
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "application/pdf" -> "pdf";
            default -> throw new IllegalArgumentException("Format file tidak didukung");
        };
    }

    public void delete(String fileName) throws IOException {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        Path file = storageDirectory.resolve(fileName).normalize();

        if (!file.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Path file tidak valid");
        }

        Files.deleteIfExists(file);
    }
}
