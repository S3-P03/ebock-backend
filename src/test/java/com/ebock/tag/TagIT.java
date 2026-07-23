package com.ebock.tag;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TagIT {

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void tagDelete_DeletesTag() {
        given()
                .pathParam("id", 4)
                .when()
                .delete("/tag/{id}")
                .then()
                .statusCode(204);
    }
}
