package com.ebock.PaymentOption;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PaymentOptionIT {

    @Test
    void queryListReturnsPaymentOptions() {
        given()
                .when()
                .get("/paymentOption")
                .then()
                .statusCode(200)
                .body("[0].paymentOptnId", is(1))
                .body("[0].name", is("Interac"));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void paymentOptionDelete_DeletesPaymentOption() {
        given()
                .pathParam("id", 2)
                .when()
                .delete("/paymentOption/{id}")
                .then()
                .statusCode(204);
    }
}
