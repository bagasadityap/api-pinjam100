package com.bagas.pinjam100.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {
    private final Path root;

    public ImageStorageService(
            @Value("${app.storage.image-dir}") String dir
    ) {
        this.root = Paths.get(dir)
                .toAbsolutePath()
                .normalize();
    }

    public String save(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("File kosong / terlalu besar");

        String ext = switch (file.getContentType()) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new IllegalArgumentException("Format tidak didukung");
        };

        if (ImageIO.read(file.getInputStream()) == null)
            throw new IllegalArgumentException("Isi file bukan gambar valid");

        Files.createDirectories(root);
        String stored = UUID.randomUUID() + "." + ext;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, root.resolve(stored), StandardCopyOption.REPLACE_EXISTING);
        }
        return stored;
    }
}
