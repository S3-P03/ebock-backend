package com.ebock.deliveryOption;

import com.ebock.business.DeliveryOption;
import com.ebock.converter.DeliveryOptionConverter;
import com.ebock.dto.request.deliveryOption.DeliveryOptionPayload;
import com.ebock.dto.response.deliveryOption.DeliveryOptionResponse;
import com.ebock.mapper.DeliveryOptionMapper;
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
public class DeliveryOptionServiceIT {

    @InjectMock
    DeliveryOptionMapper deliveryOptionMapper;

    @InjectMock
    DeliveryOptionConverter deliveryOptionConverter;

    private DeliveryOptionPayload validPayload;
    private DeliveryOption mockedDeliveryOption;
    private DeliveryOptionResponse mockedResponse;

    @BeforeEach
    public void setup() {
        validPayload = new DeliveryOptionPayload();
        validPayload.name = "asdf";

        mockedDeliveryOption = new DeliveryOption();
        mockedDeliveryOption.deliveryOptnId = 10;

        mockedResponse = new DeliveryOptionResponse();

        Mockito.when(deliveryOptionConverter.toBusiness(any(DeliveryOptionPayload.class))).thenReturn(mockedDeliveryOption);
        Mockito.when(deliveryOptionConverter.toResponse(any(DeliveryOption.class))).thenReturn(mockedResponse);
        Mockito.when(deliveryOptionConverter.toResponse(any(List.class))).thenReturn(List.of(mockedResponse));
    }

    @Test
    public void testList_PermitAll_Success() {
        // Arrange
        Mockito.when(deliveryOptionMapper.getAllDeliveryOptions()).thenReturn(List.of(mockedDeliveryOption));

        given()
                .when()
                .get("/deliveryOption/list")
                .then()
                .statusCode(200);

        Mockito.verify(deliveryOptionMapper, Mockito.times(1)).getAllDeliveryOptions();
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testInsert_Authenticated_Success() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/deliveryOption/insert")
                .then()
                .statusCode(200);

        Mockito.verify(deliveryOptionMapper, Mockito.times(1)).insert(mockedDeliveryOption);
    }

    @Test
    public void testInsert_Unauthenticated_ShouldReturn401() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/deliveryOption/insert")
                .then()
                .statusCode(401);

        Mockito.verify(deliveryOptionMapper, Mockito.never()).insert(any());
    }
}
