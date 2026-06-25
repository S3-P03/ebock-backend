package com.ebock.PaymentOption;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PaymentOptionIT {

    @Test
    void queryListReturnsPaymentOptions() {
        given()
                .when()
                .get("/paymentOption/list")
                .then()
                .statusCode(200)
                .body("[0].paymentOptnId", is(1))
                .body("[0].name", is("asdfasdf"));
    }
}
