package com.ebock;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@Tag("integration")
@QuarkusTest
public class TestItemDetails {

    @Test
    void queryItemDetailsExistingItem() {
        given()
                .pathParam("id", 4)
                .when()
                .get("/item/{id}")
                .then()
                .statusCode(200)
                .body("itemId", is(4))
                .body("name", is("Chalk"))
                .body("description", is("Chalk pour l escalade"))
                .body("price", is(67.67f))
                .body("quantity", is(1))
                .body("category", is("Sports"))
                .body("wear", is("Factory New"))
                .body("sellerCip", is("larj4236"));
    }

    @Test
    void queryItemDetailsNonExistingItemReturns404() {
        given()
                .log().all()
                .pathParam("id", 456)
                .when()
                .get("/item/{id}")
                .then()
                .statusCode(404);
    }
}
