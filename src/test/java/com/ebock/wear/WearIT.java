package com.ebock.wear;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class WearIT {

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void wearDelete_DeletesWear() {
        given()
                .pathParam("id", 5)
                .when()
                .delete("/wear/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void wearDelete_Returns404_InexistentWear() {
        given()
                .pathParam("id", 111)
                .when()
                .delete("/wear/{id}")
                .then()
                .statusCode(404);
    }
}
