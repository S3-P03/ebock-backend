package com.ebock;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;

@QuarkusTest
class TestProfilVendeur {
    @Test
    void queryItemExistingUser() {
        given()
                .pathParam("cip", "pele3157")
                .when()
                .get("/item/{cip}/storefront")
                .then()
                .statusCode(200)
                .body("[0].itemId", is(5))
                .body("[0].name", is("Prise de laptop"))
                .body("[0].price", is(10.0f))
                .body("[0].quantity", is(1))
                .body("[0].categoryId", is(2))
                .body("[0].wearId", is(1))
                .body("[0].firstName", is("Éliane"))
                .body("[0].lastName", is("Pelletier"));
    }

    @Test
    void queryUserExistingUser() {
        given()
                .pathParam("cip", "pele3157")
                .when()
                .get("/user/{cip}/storefront")
                .then()
                .statusCode(200)
                .body("firstName", is("Éliane"))
                .body("lastName", is("Pelletier"))
                .body("profilePictureUrl", is(nullValue()))
                .body("createdAt", is(notNullValue()));
    }

    @Test
    void queryItemNonExistingUserReturns404() {
        given()
                .log().all()
                .pathParam("cip", "abcd1234")
                .when()
                .get("/item/{cip}/storefront")
                .then()
                .statusCode(404);

    }

    @Test
    void queryUserNonExistingUserReturns404() {
        given()
                .log().all()
                .pathParam("cip", "abcd1234")
                .when()
                .get("/user/{cip}/storefront")
                .then()
                .statusCode(404);
    }

    @Test
    void queryItemReturnsEmptyList() {
        given()
                .pathParam("cip", "test1234")
                .when()
                .get("/item/{cip}/storefront")
                .then()
                .statusCode(200)
                .body("$", empty());
    }
}