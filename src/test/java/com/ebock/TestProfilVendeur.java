package com.ebock;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@TestSecurity(user = "pele3157", roles = {"admin"})
@QuarkusTest
class TestProfilVendeur {
    @Test
    void testRequeteItem() {
        given()
                .pathParam("cip","pele3157")
                .when()
                .get("/item/{cip}/storefront")
                .then()
                .statusCode(200)
                .body("name", is("<[Prise de laptop]>"));
    }
}