package com.ebock.wear;

import com.ebock.business.Tag;
import com.ebock.business.Wear;
import com.ebock.converter.TagConverter;
import com.ebock.converter.WearConverter;
import com.ebock.dto.request.tag.TagPayload;
import com.ebock.dto.request.wear.WearPayload;
import com.ebock.dto.response.tag.TagResponse;
import com.ebock.dto.response.wear.WearResponse;
import com.ebock.mapper.TagMapper;
import com.ebock.mapper.WearMapper;
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
public class WearServiceIT {

    @InjectMock
    WearMapper wearMapper;

    @InjectMock
    WearConverter wearConverter;

    private WearPayload validPayload;
    private Wear mockedWear;
    private WearResponse mockedResponse;

    @BeforeEach
    public void setup() {
        validPayload = new WearPayload();
        validPayload.name = "asdf";

        mockedWear = new Wear();
        mockedWear.wearId = 10;

        mockedResponse = new WearResponse();

        Mockito.when(wearConverter.toBusiness(any(WearPayload.class))).thenReturn(mockedWear);
        Mockito.when(wearConverter.toResponse(any(Wear.class))).thenReturn(mockedResponse);
        Mockito.when(wearConverter.toResponse(any(List.class))).thenReturn(List.of(mockedResponse));
    }

    @Test
    public void testList_PermitAll_Success() {
        // Arrange
        Mockito.when(wearMapper.getAllWears()).thenReturn(List.of(mockedWear));

        given()
                .when()
                .get("/wear/list")
                .then()
                .statusCode(200);

        Mockito.verify(wearMapper, Mockito.times(1)).getAllWears();
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testInsert_Authenticated_Success() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/wear/insert")
                .then()
                .statusCode(200);

        Mockito.verify(wearMapper, Mockito.times(1)).insert(mockedWear);
    }

    @Test
    public void testInsert_Unauthenticated_ShouldReturn401() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/wear/insert")
                .then()
                .statusCode(401);

        Mockito.verify(wearMapper, Mockito.never()).insert(any());
    }
}
