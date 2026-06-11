package com.ebock.image;

import com.ebock.mapper.ImageMapper;
import com.ebock.service.ImageService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@QuarkusTest
public class ImageServiceTest {

    @Inject
    ImageService imageService;

    @InjectMock
    ImageMapper imageMapper;

    @TempDir
    Path tempDir;

    @Test
    void uploadFile_JpegValide_return200(){

    }
}
