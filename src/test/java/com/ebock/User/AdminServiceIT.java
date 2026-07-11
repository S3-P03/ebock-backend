package com.ebock.User;

import com.ebock.adapter.KeycloakAdapter;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
public class AdminServiceIT {
    @Inject
    KeycloakAdapter keycloakAdapter;

    private static KeycloakAdapter mockedKeycloakAdapter;

    @BeforeEach
    void setUp() {
        if (isCiProfile() && mockedKeycloakAdapter != null) {
            reset(mockedKeycloakAdapter);
        }
    }

    @IfBuildProfile("ci")
    @Singleton
    static class CiKeycloakAdapterProducer {
        @Produces
        @Alternative
        @Priority(1)
        KeycloakAdapter keycloakAdapter() {
            if (mockedKeycloakAdapter == null) {
                mockedKeycloakAdapter = mock(KeycloakAdapter.class);
            }
            return mockedKeycloakAdapter;
        }
    }

    private static boolean isCiProfile() {
        return "ci".equalsIgnoreCase(ConfigProvider.getConfig()
                .getOptionalValue("quarkus.profile", String.class)
                .orElse(""));
    }

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
        if (isCiProfile()) {
            when(keycloakAdapter.getAllUsers()).thenReturn(List.of());
        }

        given()
                .when()
                .get("/user/list")
                .then()
                .statusCode(200)
                .body("utilisateurs", notNullValue());
    }

    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void enableUser_ShouldReturn403_WhenNotAdmin() {
        String cip = "dubw5596";

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/enable")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void enableUser_ShouldReturn404_WhenUserNotExist() {
        String cip = "aaaa1111";
        if (isCiProfile()) {
            doThrow(new NotFoundException("User not found")).when(keycloakAdapter).enableUser(cip);
        }

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/enable")
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
                .put("/user/{cip}/disable")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn200() {
        String cip = "test1234";
        if (isCiProfile()) {
            doNothing().when(keycloakAdapter).disableUser(cip);
            when(keycloakAdapter.isUserEnabled(cip)).thenReturn(false);
        }

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/disable")
                .then()
                .statusCode(200);

        if (isCiProfile()) {
            assertFalse(keycloakAdapter.isUserEnabled(cip));
        }
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn404_WhenUserNotExist() {
        String cip = "aaaa1111";
        if (isCiProfile()) {
            doThrow(new NotFoundException("User not found")).when(keycloakAdapter).disableUser(cip);
        }

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/disable")
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
    void disableUser_ShouldReturn200_WhenAlreadyDisabled() {
        String cip = "test1234";
        if (isCiProfile()) {
            doNothing().when(keycloakAdapter).disableUser(cip);
        }

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/disable")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn403_WhenUserAdmin() {
        String cip = "dubw5596";
        if (isCiProfile()) {
            doThrow(new ForbiddenException("Cannot disable an admin")).when(keycloakAdapter).disableUser(cip);
        }

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/disable")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void enableUser_ShouldReturn403_WhenUserAdmin() {
        String cip = "dubw5596";
        if (isCiProfile()) {
            doThrow(new ForbiddenException("Cannot enable an admin")).when(keycloakAdapter).enableUser(cip);
        }

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/enable")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void disableUser_ShouldReturn400_WhenCipTooLong() {
        String cip = "dubw559655965";
        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/disable")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void enableUser_ShouldReturn400_WhenCipTooLong() {
        String cip = "dubw559655965";
        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/enable")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void enableUser_ShouldReturn200() {
        String cip = "test1234";
        if (isCiProfile()) {
            doNothing().when(keycloakAdapter).enableUser(cip);
            when(keycloakAdapter.isUserEnabled(cip)).thenReturn(true);
        }

        given()
                .pathParam("cip", cip)
                .when()
                .put("/user/{cip}/enable")
                .then()
                .statusCode(200);

        if (isCiProfile()) {
            assertTrue(keycloakAdapter.isUserEnabled(cip));
        }
    }

}
