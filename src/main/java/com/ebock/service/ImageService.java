package com.ebock.service;

import com.ebock.dto.request.image.ImagePayload;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path; // Keep this import for the @Path annotation
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Path("/image")
public class ImageService {

    @Inject
    @ConfigProperty(name = "image.upload.directory")
    String uploadDir;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @Blocking
    public Response uploadFile(ImagePayload data) {
        if (data.file == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing file").build();
        }

        try {
            java.nio.file.Path outputPath = java.nio.file.Path.of(uploadDir);

            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            String originalFileName = data.file.fileName();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String guidFileName = UUID.randomUUID().toString() + fileExtension;

            java.nio.file.Path targetFile = outputPath.resolve(guidFileName);

            Files.move(data.file.uploadedFile(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return Response.ok("{\"fileName\":\"" + guidFileName + "\"}").build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Upload failed").build();
        }
    }
}