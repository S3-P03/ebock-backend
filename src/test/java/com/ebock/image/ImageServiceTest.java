package com.ebock.image;

import com.ebock.business.Image;
import com.ebock.dto.request.image.ImagePayload;
import com.ebock.dto.response.image.ItemImageResponse;
import com.ebock.dto.response.item.ItemResponse;
import com.ebock.mapper.ImageMapper;
import com.ebock.mapper.ItemMapper;
import com.ebock.dto.response.image.ImageUploadResponse;
import com.ebock.service.IImageStorageService;
import com.ebock.service.ImageService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.Test;
import io.quarkus.test.InjectMock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ImageServiceTest {
    @Inject
    ImageService imageService;

    @InjectMock
    ImageMapper imageMapper;

    @InjectMock
    ItemMapper itemMapper;

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
        ImageUploadResponse response = imageService.uploadFile(payload);

        // Assert
        assertNotNull(response);
        assertFalse(response.guid.isEmpty());

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
        ImageUploadResponse response = imageService.uploadFile(payload);

        // Assert
        assertNotNull(response);
        assertFalse(response.guid.isEmpty());

        Mockito.verify(imageStorageService, Mockito.times(1))
                .moveFile(Mockito.eq(fakePath), Mockito.endsWith(".png"));

        Mockito.verify(imageMapper, Mockito.times(1))
                .insert(Mockito.any(Image.class));
    }

    @Test
    @TestSecurity(user = "test", roles = {"user"})
    void uploadFile_FormatInvalide_throwException() throws IOException {
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

        // Act and assert
        assertThrows(UnsupportedOperationException.class, () -> {
            imageService.uploadFile(payload);
        });

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

        // Act and assert
        assertThrows(IllegalArgumentException.class, () -> {
            imageService.uploadFile(payload);
        });
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

        // Act and assert
        assertThrows(RuntimeException.class, () -> {
            imageService.uploadFile(payload);
        });

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

        // Mock return of the mapper
        Mockito.when(imageMapper.getImageFromGuid(mockImage.guid)).thenReturn(mockImage);

        // Fake the file return
        Path fakePath = Path.of("idk/" + mockImage.guid + ".png");

        Mockito.when(imageStorageService.getPath(mockImage.guid + ".png")).thenReturn(fakePath);

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

        // Mock return of the mapper
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

        // Mock return of the mapper
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

    @TestSecurity(user = "test", roles = {"user"})
    void getImage_NotAFile_return404() {
        // Arrange
        // Create the image and mock service
        Image mockImage = new Image();
        mockImage.guid = UUID.randomUUID().toString();
        mockImage.fileExtension = ".png";
        mockImage.originalFilename = "idk";
        String filename = mockImage.guid + mockImage.fileExtension;

        // Mock return of the mapper
        Mockito.when(imageMapper.getImageFromGuid(mockImage.guid)).thenReturn(mockImage);

        // Fake the file return
        Path fakePath = Path.of("idk/" + mockImage.guid + ".png");
        Mockito.when(imageStorageService.getPath(Mockito.anyString())).thenReturn(fakePath);

        // Fake file validity
        Mockito.when(imageStorageService.exists(fakePath)).thenReturn(true);
        Mockito.when(imageStorageService.isRegularFile(fakePath)).thenReturn(false);

        // Act
        Response response = imageService.getImage(mockImage.guid);

        // Assert
        assertEquals(404, response.getStatus());
    }

    @Test
    void testItemImagesReturnsListOfImages() {
        // arrange
        List<ItemImageResponse> expected = new ArrayList<>();
        //Contenu dans la liste pour éviter le flag en cas de liste vide ou null
        expected.add(new ItemImageResponse());
        when(itemMapper.getItemCountById(1)).thenReturn(1);
        when(imageMapper.getItemImages(1)).thenReturn(expected);

        // act
        List<ItemImageResponse> result = imageService.getItemImages(1);

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testItemImagesUnknownItemThrowsNotFound() {
        // arrange
        when(itemMapper.getItemCountById(1)).thenReturn(0);

        // act and assert
        assertThrows(NotFoundException.class, () -> imageService.getItemImages(1));
    }
}
