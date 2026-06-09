package com.ebock;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@Tag("integration")
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
                .body("[0].name", is("Prise de laptop"))
                .body("[0].description", is("Une prise de laptop vraiment longue"))
                .body("[0].price", is(10.0f))
                .body("[0].quantity", is(1))
                .body("[0].categoryId", is(2))
                .body("[0].wearId", is(1))
                .body("[0].firstName", is("Éliane"))
                .body("[0].lastName", is("Pelletier"));
    }

    @Test
    void testRequeteUser() {
        given()
                .pathParam("cip","pele3157")
                .when()
                .get("/user/{cip}/storefront")
                .then()
                .statusCode(200)
                .body("firstName", is("Éliane"))
                .body("lastName", is("Pelletier"))
                .body("profilePictureUrl", is(nullValue()))
                .body("createdAt", is(notNullValue()));
    }
}