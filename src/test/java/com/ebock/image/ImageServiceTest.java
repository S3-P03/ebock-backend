package com.ebock.image;

import com.ebock.business.Image;
import com.ebock.dto.request.image.ImagePayload;
import com.ebock.mapper.ImageMapper;
import com.ebock.service.IImageStorageService;
import com.ebock.service.ImageService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.Test;
import io.quarkus.test.InjectMock;
import java.nio.file.Path;
import java.util.Map;
import org.mockito.Mockito;

@QuarkusTest
public class ImageServiceTest {

    @Inject
    ImageService imageService;

    @InjectMock
    ImageMapper imageMapper;

    @InjectMock
    IImageStorageService imageStorageService;

    @Test
    void uploadFile_JpegValide_return200(){
        // Create and mock payload data
        FileUpload mockFileUpload = Mockito.mock(FileUpload.class);
        Path fakePath = Path.of("temp.idk");

        Mockito.when(mockFileUpload.uploadedFile()).thenReturn(fakePath);
        Mockito.when(mockFileUpload.fileName()).thenReturn("pic.jpg");

        // Make the payload
        ImagePayload payload = new ImagePayload();
        payload.file = mockFileUpload;

        Mockito.when()
    }
}
