package com.ebock.tag;

import com.ebock.business.Tag;
import com.ebock.converter.TagConverter;
import com.ebock.dto.request.tag.TagPayload;
import com.ebock.dto.response.tag.TagResponse;
import com.ebock.mapper.TagMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
public class TagServiceIT {

    @InjectMock
    TagMapper tagMapper;

    @InjectMock
    TagConverter tagConverter;

    private TagPayload validPayload;
    private Tag mockedTag;
    private TagResponse mockedResponse;

    @BeforeEach
    public void setup() {
        validPayload = new TagPayload();
        validPayload.name = "asdf";

        mockedTag = new Tag();
        mockedTag.tagId = 10;

        mockedResponse = new TagResponse();

        Mockito.when(tagConverter.toBusiness(any(TagPayload.class))).thenReturn(mockedTag);
        Mockito.when(tagConverter.toResponse(any(Tag.class))).thenReturn(mockedResponse);
        Mockito.when(tagConverter.toResponse(any(List.class))).thenReturn(List.of(mockedResponse));
    }

    @Test
    public void testList_PermitAll_Success() {
        // Arrange
        Mockito.when(tagMapper.getAllTags()).thenReturn(List.of(mockedTag));

        given()
                .when()
                .get("/tag/list")
                .then()
                .statusCode(200);

        Mockito.verify(tagMapper, Mockito.times(1)).getAllTags();
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testInsert_Authenticated_Success() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/tag/insert")
                .then()
                .statusCode(200);

        Mockito.verify(tagMapper, Mockito.times(1)).insert(mockedTag);
    }

    @Test
    public void testInsert_Unauthenticated_ShouldReturn401() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/tag/insert")
                .then()
                .statusCode(401);

        Mockito.verify(tagMapper, Mockito.never()).insert(any());
    }
}
