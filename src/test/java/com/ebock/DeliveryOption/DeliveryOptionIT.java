package com.ebock.DeliveryOption;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class DeliveryOptionIT {

    @Test
    void queryListReturnsDeliveryOptions() {
        given()
                .when()
                .get("/deliveryOption/list")
                .then()
                .statusCode(200)
                .body("[0].deliveryOptnId", is(1))
                .body("[0].name", is("Livraison"));
    }
}
