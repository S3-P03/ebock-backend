package com.ebock.service;

import com.ebock.business.Image;
import com.ebock.dto.request.image.ImagePayload;
import com.ebock.dto.response.image.ItemImageResponse;
import com.ebock.mapper.ImageMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.dto.response.image.ImageUploadResponse;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/image")
public class ImageService {
    @Inject
    ImageMapper imageMapper;

    @Inject
    ItemMapper itemMapper;

    @Inject
    IImageStorageService imageStorageService;

    private static final Map<String, String> MIME_TO_EXTENSION = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public ImageUploadResponse uploadFile(ImagePayload data) {
        if (data.file == null) {
            throw new IllegalArgumentException("No file");
        }

        try {
            String mimeType = imageStorageService.detectMimeType(data.file.uploadedFile());
            if(mimeType == null){
                imageStorageService.deleteFile(data.file.uploadedFile());
                throw new UnsupportedOperationException("Invalid file");
            }

            String guid = UUID.randomUUID().toString();
            String extension = MIME_TO_EXTENSION.get(mimeType);
            String filename = guid + extension;

            // Move the file
            imageStorageService.moveFile(data.file.uploadedFile(), filename);

            // Create the record in the db
            Image image = new Image();
            image.originalFilename = data.file.fileName();
            image.fileExtension = extension;
            image.guid = guid;
            imageMapper.insert(image);

            // Return
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.guid = guid;

            return imageUploadResponse;
        } catch (IOException e) {
            // Cleanup the file
            try {
                imageStorageService.deleteFile(data.file.uploadedFile());
            } catch (IOException ignored) {}

            throw new RuntimeException("Upload failed", e);
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
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Image image = imageMapper.getImageFromGuid(guid);
        if(image == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String filename = image.guid + image.fileExtension;
        java.nio.file.Path requestedFile = imageStorageService.getPath(filename);

        // Check the file exists
        if (!imageStorageService.exists(requestedFile) || !imageStorageService.isRegularFile(requestedFile)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Image not found").build();
        }

        // Get MIME type
        String mimeType = image.fileExtension.equalsIgnoreCase(".png") ? "image/png" : "image/jpeg";

        // Use original filename
        String downloadName = image.originalFilename != null ? image.originalFilename : "image" + image.fileExtension;
        String safeDownloadName = downloadName.replaceAll("[^a-zA-Z0-9._-]", "_");

        return Response.ok(requestedFile.toFile())
                .type(mimeType)
                .header("Content-Disposition", "inline; filename=\"" + safeDownloadName + "\"")
                .build();
    }

    @GET
    @Path("/forItem/{id}")
    @PermitAll
    @Blocking
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemImageResponse> getItemImages(@PathParam("id") int id) {
        if(itemMapper.getItemCountById(id) == 0)
            throw new NotFoundException("Item not found");

        List<ItemImageResponse> images = imageMapper.getItemImages(id);

        if (images == null || images.isEmpty()) {
            throw new NotFoundException("Images not found");
        }

        return images;
    }
}