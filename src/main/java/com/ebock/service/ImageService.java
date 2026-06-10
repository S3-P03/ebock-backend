package com.ebock.service;

import com.ebock.dto.request.image.ImagePayload;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Path("/image")
public class ImageService {

    @Inject
    @ConfigProperty(name = "image.upload.directory")
    String uploadDir;

    private static final Map<String, String> MIME_TO_EXTENSION = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @Blocking
    public Response uploadFile(ImagePayload data) {
        if (data.file == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No file").build();
        }

        try {
            java.nio.file.Path tempFilePath = data.file.uploadedFile();

            // Check Mime type
            String mimeType = detectMimeType(tempFilePath);
            if (mimeType == null) {
                Files.deleteIfExists(tempFilePath);
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                        .entity("Invalid file type")
                        .build();
            }

            java.nio.file.Path outputPath = java.nio.file.Path.of(uploadDir);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            // Get file extension from the MIME
            String fileExtension = MIME_TO_EXTENSION.get(mimeType);
            String guid = UUID.randomUUID().toString();
            String guidFileName = guid + fileExtension;
            java.nio.file.Path targetFile = outputPath.resolve(guidFileName);

            Files.move(data.file.uploadedFile(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return Response.ok("{\"guid\":\"" + guid + "\"}").build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Upload failed").build();
        }
    }

    @GET
    @Path("/{fileName}")
    @PermitAll
    @Blocking
    public Response getImage(@PathParam("fileName") String fileName) {
        // Check the filename is valid
        if (fileName == null || fileName.isBlank() || !fileName.matches("[a-zA-Z0-9._-]+")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid filename").build();
        }

        try {
            // Get the safe path
            java.nio.file.Path baseDir = java.nio.file.Path.of(uploadDir).toRealPath();
            java.nio.file.Path requestedFile = baseDir.resolve(fileName).normalize();

            // Check the file exists
            if (!Files.exists(requestedFile) || !Files.isRegularFile(requestedFile)) {
                return Response.status(Response.Status.NOT_FOUND).entity("Image not found").build();
            }

            // Get the real filename
            java.nio.file.Path realFile = requestedFile.toRealPath();
            if (!realFile.startsWith(baseDir)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid filename").build();
            }

            // Check the MIME type
            String mimeType = detectMimeType(realFile);
            if (mimeType == null) {
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            }

            // Make sure the filename is safe
            String safeFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

            return Response.ok(realFile.toFile())
                    .type(mimeType)
                    .header("Content-Disposition", "inline; filename=\"" + safeFileName + "\"")
                    .build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving file").build();
        }
    }

    private String detectMimeType(java.nio.file.Path filePath) throws IOException {
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
        }
    }
}