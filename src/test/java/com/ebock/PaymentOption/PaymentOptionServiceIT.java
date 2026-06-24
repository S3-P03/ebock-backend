package com.ebock.PaymentOption;

import com.ebock.business.PaymentOption;
import com.ebock.converter.PaymentOptionConverter;
import com.ebock.dto.request.paymentOption.PaymentOptionPayload;
import com.ebock.dto.response.paymentOption.PaymentOptionResponse;
import com.ebock.mapper.PaymentOptionMapper;
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
public class PaymentOptionServiceIT {

    @InjectMock
    PaymentOptionMapper paymentOptionMapper;

    @InjectMock
    PaymentOptionConverter paymentOptionConverter;

    private PaymentOptionPayload validPayload;
    private PaymentOption mockedPaymentOption;
    private PaymentOptionResponse mockedResponse;

    @BeforeEach
    public void setup() {
        validPayload = new PaymentOptionPayload();
        validPayload.name = "asdf";

        mockedPaymentOption = new PaymentOption();
        mockedPaymentOption.paymentOptnId = 10;

        mockedResponse = new PaymentOptionResponse();

        Mockito.when(paymentOptionConverter.toBusiness(any(PaymentOptionPayload.class))).thenReturn(mockedPaymentOption);
        Mockito.when(paymentOptionConverter.toResponse(any(PaymentOption.class))).thenReturn(mockedResponse);
        Mockito.when(paymentOptionConverter.toResponse(any(List.class))).thenReturn(List.of(mockedResponse));
    }

    @Test
    public void testList_PermitAll_Success() {
        // Arrange
        Mockito.when(paymentOptionMapper.getAllPaymentOptions()).thenReturn(List.of(mockedPaymentOption));

        given()
                .when()
                .get("/paymentOption/list")
                .then()
                .statusCode(200);

        Mockito.verify(paymentOptionMapper, Mockito.times(1)).getAllPaymentOptions();
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testInsert_Authenticated_Success() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/paymentOption/insert")
                .then()
                .statusCode(200);

        Mockito.verify(paymentOptionMapper, Mockito.times(1)).insert(mockedPaymentOption);
    }

    @Test
    public void testInsert_Unauthenticated_ShouldReturn401() {
        given()
                .contentType(ContentType.JSON)
                .body(validPayload)
                .when()
                .post("/paymentOption/insert")
                .then()
                .statusCode(401);

        Mockito.verify(paymentOptionMapper, Mockito.never()).insert(any());
    }
}
