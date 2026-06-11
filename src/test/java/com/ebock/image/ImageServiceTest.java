package com.ebock.image;

import com.ebock.business.Image;
import com.ebock.dto.request.image.ImagePayload;
import com.ebock.mapper.ImageMapper;
import com.ebock.service.IImageStorageService;
import com.ebock.service.ImageService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.Test;
import io.quarkus.test.InjectMock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ImageServiceTest {

    @Inject
    ImageService imageService;

    @InjectMock
    ImageMapper imageMapper;

    @InjectMock
    IImageStorageService imageStorageService;

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void uploadFile_JpegValide_return200() throws IOException {
        // Arrange
        // Create and mock payload data
        FileUpload mockFileUpload = Mockito.mock(FileUpload.class);
        Path fakePath = Path.of("temp.idk");

        Mockito.when(mockFileUpload.uploadedFile()).thenReturn(fakePath);
        Mockito.when(mockFileUpload.fileName()).thenReturn("pic.jpg");

        // Make the payload
        ImagePayload payload = new ImagePayload();
        payload.file = mockFileUpload;

        Mockito.when(imageStorageService.detectMimeType(fakePath)).thenReturn("image/jpeg");

        // Act
        Response response = imageService.uploadFile(payload);

        // Assert
        assertEquals(200, response.getStatus());

        Mockito.verify(imageStorageService, Mockito.times(1))
                .moveFile(Mockito.eq(fakePath), Mockito.endsWith(".jpg"));

        Mockito.verify(imageMapper, Mockito.times(1))
                .insert(Mockito.any(Image.class));
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void uploadFile_PngValide_return200() throws IOException {
        // Arrange
        // Create and mock payload data
        FileUpload mockFileUpload = Mockito.mock(FileUpload.class);
        Path fakePath = Path.of("temp.idk");

        Mockito.when(mockFileUpload.uploadedFile()).thenReturn(fakePath);
        Mockito.when(mockFileUpload.fileName()).thenReturn("pic.png");

        // Make the payload
        ImagePayload payload = new ImagePayload();
        payload.file = mockFileUpload;

        Mockito.when(imageStorageService.detectMimeType(fakePath)).thenReturn("image/png");

        // Act
        Response response = imageService.uploadFile(payload);

        // Assert
        assertEquals(200, response.getStatus());

        Mockito.verify(imageStorageService, Mockito.times(1))
                .moveFile(Mockito.eq(fakePath), Mockito.endsWith(".png"));

        Mockito.verify(imageMapper, Mockito.times(1))
                .insert(Mockito.any(Image.class));
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void uploadFile_FormatInvalide_return415() throws IOException {
        // Arrange
        // Create and mock payload data
        FileUpload mockFileUpload = Mockito.mock(FileUpload.class);
        Path fakePath = Path.of("temp.idk");

        Mockito.when(mockFileUpload.uploadedFile()).thenReturn(fakePath);
        Mockito.when(mockFileUpload.fileName()).thenReturn("pic.invalide");

        // Make the payload
        ImagePayload payload = new ImagePayload();
        payload.file = mockFileUpload;

        Mockito.when(imageStorageService.detectMimeType(fakePath)).thenReturn(null);

        // Act
        Response response = imageService.uploadFile(payload);

        // Assert
        assertEquals(415, response.getStatus());

        Mockito.verify(imageStorageService, Mockito.times(1))
                .deleteFile(Mockito.eq(fakePath));
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void uploadFile_PayloadVide_Return400() throws IOException {
        // Arrange
        // Make the payload
        ImagePayload payload = new ImagePayload();
        payload.file = null;

        // Act
        Response response = imageService.uploadFile(payload);

        // Assert
        assertEquals(400, response.getStatus());
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void uploadFile_ThrowError_Return500AndDelete() throws IOException {
        // Arrange
        // Create and mock payload data
        FileUpload mockFileUpload = Mockito.mock(FileUpload.class);
        Path fakePath = Path.of("temp.idk");

        Mockito.when(mockFileUpload.uploadedFile()).thenReturn(fakePath);
        Mockito.when(mockFileUpload.fileName()).thenReturn("pic.png");

        // Make the payload
        ImagePayload payload = new ImagePayload();
        payload.file = mockFileUpload;

        Mockito.when(imageStorageService.detectMimeType(Mockito.any())).thenThrow(new IOException("Erreur ben random"));

        // Act
        Response response = imageService.uploadFile(payload);

        // Assert
        assertEquals(500, response.getStatus());

        Mockito.verify(imageStorageService, Mockito.times(1)).deleteFile(Mockito.eq(fakePath));
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void getImage_GuidValide_return200() {
        // Arrange
        // Create the image and mock service
        Image mockImage = new Image();
        mockImage.guid = UUID.randomUUID().toString();
        mockImage.fileExtension = ".png";
        mockImage.originalFilename = "idk";
        String filename = mockImage.guid + mockImage.fileExtension;

        // Mock return of the service
        Mockito.when(imageMapper.getImageFromGuid(mockImage.guid)).thenReturn(mockImage);

        // Fake the file return
        Path fakePath = Path.of("idk/" + mockImage.guid + ".png");
        Mockito.when(imageStorageService.getPath(Mockito.anyString())).thenReturn(fakePath);

        // Fake file validity
        Mockito.when(imageStorageService.exists(fakePath)).thenReturn(true);
        Mockito.when(imageStorageService.isRegularFile(fakePath)).thenReturn(true);

        // Act
        Response response = imageService.getImage(mockImage.guid);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("image/png", response.getMediaType().toString());
        File responseFile = (File)response.getEntity();
        assertEquals(fakePath.toFile().getPath(), responseFile.getPath());
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void getImage_UUIDInvalid_return400() {
        // Arrange

        // Act
        Response response = imageService.getImage("/asdf/invalide");

        // Assert
        assertEquals(400, response.getStatus());
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void getImage_ImageNotInDb_return404() {
        // Arrange
        String uuid = UUID.randomUUID().toString();

        // Mock return of the service
        Mockito.when(imageMapper.getImageFromGuid(uuid)).thenReturn(null);

        // Act
        Response response = imageService.getImage(uuid);

        // Assert
        assertEquals(404, response.getStatus());
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void getImage_FileDoesntExist_return404() {
        // Arrange
        // Create the image and mock service
        Image mockImage = new Image();
        mockImage.guid = UUID.randomUUID().toString();
        mockImage.fileExtension = ".png";
        mockImage.originalFilename = "idk";
        String filename = mockImage.guid + mockImage.fileExtension;

        // Mock return of the service
        Mockito.when(imageMapper.getImageFromGuid(mockImage.guid)).thenReturn(mockImage);

        // Fake the file return
        Path fakePath = Path.of("idk/" + mockImage.guid + ".png");
        Mockito.when(imageStorageService.getPath(Mockito.anyString())).thenReturn(fakePath);

        // Fake file validity
        Mockito.when(imageStorageService.exists(fakePath)).thenReturn(false);
        Mockito.when(imageStorageService.isRegularFile(fakePath)).thenReturn(true);

        // Act
        Response response = imageService.getImage(mockImage.guid);

        // Assert
        assertEquals(404, response.getStatus());
    }
}
