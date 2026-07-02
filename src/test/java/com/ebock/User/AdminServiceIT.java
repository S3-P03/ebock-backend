package com.ebock.User;

import com.ebock.adapter.KeycloakAdapter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
public class AdminServiceIT {
    @Inject
    KeycloakAdapter keycloakAdapter;

    @Test
    @TestSecurity(user = "user", roles = {"student"})
    void listUser_ShouldReturn403_WhenNotAdmin() {
        given()
                .when()
                .get("/user/list/")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void listUser_ShouldReturn200_WhenAdmin() {
        given()
                .when()
                .get("/user/list/")
                .then()
                .statusCode(200)
                .body("utilisateurs", notNullValue());
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void enableUser_ShouldReturn200() {
        String cip = "dubw5596";

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/enable/{cip}")
                .then()
                .statusCode(200);

        assertTrue(keycloakAdapter.isUserEnabled(cip));
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void enableUser_ShouldReturn403_WhenNotAdmin() {
        String cip = "dubw5596";

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/enable/{cip}")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void enableUser_ShouldReturn404_WhenUserNotExist() {
        String cip = "aaaa1111";

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/enable/{cip}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void disableUser_ShouldReturn403_WhenNotAdmin() {
        String cip = "dubw5596";

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/disable/{cip}")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn200() {
        String cip = "dubw5596";

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/disable/{cip}")
                .then()
                .statusCode(200);

        assertFalse(keycloakAdapter.isUserEnabled(cip));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn404_WhenUserNotExist() {
        String cip = "aaaa1111";

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/disable/{cip}")
                .then()
                .statusCode(404);
    }

    @Test
    void listUser_ShouldReturn401_WhenUnauthenticated() {
        given()
                .when()
                .get("/user/list/")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void enableUser_ShouldReturn400_WhenCipTooLong() {
        String cip = "dubw559655965";
        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/enable/{cip}")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn200_WhenAlreadyDisabled() {
        String cip = "dubw5596";
        keycloakAdapter.disableUser(cip);

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/disable/{cip}")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn400_WhenCipTooLong() {
        String cip = "dubw559655965";
        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/disable/{cip}")
                .then()
                .statusCode(400);
    }

}
