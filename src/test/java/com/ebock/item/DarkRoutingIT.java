package com.ebock.item;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class DarkRoutingIT {
    @Test
    @TestSecurity(user = "dubw5596")
    public void testNormal_NoHeader_ReturnsEbockData() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/item/4")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("name", equalTo("Chalk"));
    }

    @Test
    @TestSecurity(user = "dubw5596", roles = {"dark"})
    public void testDark_NoHeader_ReturnsDarkEbockData() {
        given()
                .contentType(ContentType.JSON)
                .header("Environment", "dark_ebock")
                .when()
                .get("/item/4")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("name", equalTo("Carte Cupidon Loup-Garou"));
    }

    @Test
    @TestSecurity(user = "dubw5596", roles = {"dark"})
    public void testConnectionBleed_SeperateSchema() {
        given()
                .contentType(ContentType.JSON)
                .header("Environment", "dark_ebock")
                .when()
                .get("/item/4")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("name", equalTo("Carte Cupidon Loup-Garou"));

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/item/4")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("name", equalTo("Chalk"));
    }

    @Test
    @TestSecurity(user = "dubw5596")
    public void testForbidden_WithoutDarkRole_Returns403() {
        given()
                .contentType(ContentType.JSON)
                .header("Environment", "dark_ebock")
                .when()
                .get("/item/4")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "dubw5596")
    public void testSecurityRejection_InvalidName_Returns403() {
        given()
                .contentType(ContentType.JSON)
                .header("Environment", "aaaaaaaaa")
                .when()
                .get("/item/4")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }
}
