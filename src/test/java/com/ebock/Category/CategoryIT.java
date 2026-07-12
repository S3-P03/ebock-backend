package com.ebock.Category;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CategoryIT {

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void categoryDelete_DeletesCategory() {
        given()
                .pathParam("id", 10)
                .when()
                .delete("/category/{id}")
                .then()
                .statusCode(204);
    }
}
