package com.ebock.service;

import com.ebock.business.Image;
import com.ebock.dto.request.image.ImagePayload;
import com.ebock.mapper.ImageMapper;
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

    @Inject
    ImageMapper imageMapper;

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

        java.nio.file.Path tempFilePath = data.file.uploadedFile();
        java.nio.file.Path targetFile = null;

        try {
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
            targetFile = outputPath.resolve(guidFileName);

            // Move the file
            Files.move(data.file.uploadedFile(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            // Create the record in the db
            Image image = new Image();
            image.originalFilename = data.file.fileName();
            image.fileExtension = fileExtension;
            image.guid = guid;
            imageMapper.insert(image);

            return Response.ok(Map.of("guid", guid)).build();
        } catch (IOException e) {
            // Cleanup the file
            if (targetFile != null) {
                try { Files.deleteIfExists(targetFile); } catch (IOException ignored) {}
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Upload failed").build();
        }
    }

    @GET
    @Path("/{guid}")
    @PermitAll
    @Blocking
    public Response getImage(@PathParam("guid") String guid) {
        // Check the guid is valid
        try {
            UUID.fromString(guid);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid GUID format").build();
        }

        Image image = imageMapper.getImageFromGuid(guid);
        if(image == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Image not found").build();
        }

        String filename = image.guid + image.fileExtension;

        try {
            // Get the safe path
            java.nio.file.Path baseDir = java.nio.file.Path.of(uploadDir).toRealPath();
            java.nio.file.Path requestedFile = baseDir.resolve(filename).normalize();

            // Check the file exists
            if (!Files.exists(requestedFile) || !Files.isRegularFile(requestedFile)) {
                return Response.status(Response.Status.NOT_FOUND).entity("Image not found").build();
            }

            // Check for path traversal
            java.nio.file.Path realFile = requestedFile.toRealPath();
            if (!realFile.startsWith(baseDir)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Invalid filename").build();
            }

            // Get MIME type
            String mimeType = image.fileExtension.equalsIgnoreCase(".png") ? "image/png" : "image/jpeg";

            // Use original filename
            String downloadName = image.originalFilename != null ? image.originalFilename : "image" + image.fileExtension;
            String safeDownloadName = downloadName.replaceAll("[^a-zA-Z0-9._-]", "_");

            return Response.ok(realFile.toFile())
                    .type(mimeType)
                    .header("Content-Disposition", "inline; filename=\"" + safeDownloadName + "\"")
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