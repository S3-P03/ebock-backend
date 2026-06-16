package com.ebock.image;

import com.ebock.business.Image;
import com.ebock.mapper.ImageMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
class ImageIT {
    @InjectMock
    ImageMapper imageMapper;

    private static File validFile;

    @ConfigProperty(name = "image.upload.directory")
    String uploadDir;

    @BeforeAll
    static void setup() throws IOException {
        validFile = File.createTempFile("img", ".jpg");
        byte[] header = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00};
        FileOutputStream fos = new FileOutputStream(validFile);
        fos.write(header);
    }

    @AfterAll
    static void delete() {
        if (validFile != null && validFile.exists()) {
            validFile.delete();
        }
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"user"})
    void uploadFile_ValidMultipart_Returns200AndGuid() {
        // Arrange
        Mockito.doNothing().when(imageMapper).insert(Mockito.any(Image.class));

        // Act
        String responseGuid = given()
                .multiPart("file", validFile, "image/jpeg")
                .when()
                .post("/image/upload")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("guid", notNullValue())
                .extract().path("guid");

        // Assert
        Path expectedSavedFile = Path.of(uploadDir, responseGuid + ".jpg");
        assertTrue(Files.exists(expectedSavedFile));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"user"})
    void getImage_ValidGuid_Returns200AndFile() throws IOException {
        // Arrange
        String guid = UUID.randomUUID().toString();
        String filename = guid + ".jpg";
        Path targetPath = Path.of(uploadDir, filename);
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, new byte[]{0x01, 0x02, 0x03});

        // Mock the image
        Image mockImage = new Image();
        mockImage.guid = guid;
        mockImage.fileExtension = ".jpg";
        mockImage.originalFilename = "test.jpg";
        Mockito.when(imageMapper.getImageFromGuid(guid)).thenReturn(mockImage);

        // Assert + Act
        byte[] responseBytes = given()
                .pathParam("guid", guid)
                .when()
                .get("/image/{guid}")
                .then()
                .statusCode(200)
                .contentType("image/jpeg")
                .extract()
                .asByteArray();

        assertArrayEquals(new byte[]{0x01, 0x02, 0x03}, responseBytes);
    }

}