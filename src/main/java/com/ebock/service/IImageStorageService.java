package com.ebock.service;

import java.io.IOException;
import java.nio.file.Path;

public interface IImageStorageService {
    void moveFile(Path tempPath, String targetFileName) throws IOException;
    void deleteFile(Path path) throws IOException;
    Path getPath(String fileName);
    boolean exists(Path path);
    boolean isRegularFile(Path path);
    String detectMimeType(Path filePath) throws IOException;
}