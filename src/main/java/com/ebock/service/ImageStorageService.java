package com.ebock.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@ApplicationScoped
public class ImageStorageService implements IImageStorageService {

    @ConfigProperty(name = "image.upload.directory")
    String uploadDir;

    @Override
    public void moveFile(Path tempPath, String targetFileName) throws IOException {
        Path outputPath = Path.of(uploadDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        Files.move(tempPath, outputPath.resolve(targetFileName), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void deleteFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    @Override
    public Path getPath(String fileName) {
        return Path.of(uploadDir).resolve(fileName);
    }

    @Override
    public String detectMimeType(Path filePath) {
        try (InputStream is = Files.newInputStream(filePath)) {
            byte[] header = is.readNBytes(8);
            if (header.length < 4) return null;

            // JPEG: FF D8 FF
            if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF)
                return "image/jpeg";

            // PNG: 89 50 4E 47
            if ((header[0] & 0xFF) == 0x89 && (header[1] & 0xFF) == 0x50 &&
                    (header[2] & 0xFF) == 0x4E && (header[3] & 0xFF) == 0x47)
                return "image/png";

            return null;
        } catch (IOException e) {
            return null;
        }
    }
}