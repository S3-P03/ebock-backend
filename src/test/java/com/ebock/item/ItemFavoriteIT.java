package com.ebock.item;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ItemFavoriteIT {

    @InjectMock
    SecurityContext securityContext;

    @BeforeEach
    void setup() {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("larj4236");
        when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
    }

    @Test
    @TestSecurity(user = "larj4236")
    public void testFavorite_Success() {
        // Act and assert
        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .when()
                .post("/item/{id}/favorite")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "larj4236")
    public void testFavorite_UnknownItem_ShouldReturn404() {
        // Act and assert
        given()
                .pathParam("id", 87)
                .contentType(ContentType.JSON)
                .when()
                .post("/item/{id}/favorite")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @TestSecurity(user = "bad1234")
    public void testFavorite_Unauthorized_ShouldReturn401() {
        when(securityContext.getUserPrincipal().getName()).thenReturn("bad1234");
        // Act and assert
        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .when()
                .post("/item/{id}/favorite")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "larj4236")
    public void testUnfavorite_Success() {
        // Act and assert
        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .when()
                .post("/item/{id}/unfavorite")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @TestSecurity(user = "larj4236")
    public void testUnfavorite_UnknownItem_ShouldReturn404() {
        // Act and assert
        given()
                .pathParam("id", 87)
                .contentType(ContentType.JSON)
                .when()
                .post("/item/{id}/unfavorite")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @TestSecurity(user = "bad1234")
    public void testUnfavorite_Unauthorized_ShouldReturn401() {
        when(securityContext.getUserPrincipal().getName()).thenReturn("bad1234");
        // Act and assert
        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .when()
                .post("/item/{id}/unfavorite")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
