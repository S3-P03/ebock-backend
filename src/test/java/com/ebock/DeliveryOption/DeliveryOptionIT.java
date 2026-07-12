package com.ebock.DeliveryOption;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class DeliveryOptionIT {

    @Test
    void queryListReturnsDeliveryOptions() {
        given()
                .when()
                .get("/deliveryOption")
                .then()
                .statusCode(200)
                .body("[0].deliveryOptnId", is(1))
                .body("[0].name", is("Livraison"));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void deliveryOptionDelete_DeletesDeliveryOption() {
        given()
                .pathParam("id", 3)
                .when()
                .delete("/deliveryOption/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void deliveryOptionDelete_Returns404_InexistentDeliveryOption() {
        given()
                .pathParam("id", 111)
                .when()
                .delete("/deliveryOption/{id}")
                .then()
                .statusCode(404);
    }
}
