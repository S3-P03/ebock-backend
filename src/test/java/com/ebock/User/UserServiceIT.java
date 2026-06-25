package com.ebock.User;

import com.ebock.adapter.KeycloakAdapter;
import com.ebock.business.User;
import com.ebock.dto.request.user.AddressPayload;
import com.ebock.dto.request.user.UserChangePasswordPayload;
import com.ebock.dto.request.user.UserEditPayload;
import com.ebock.mapper.AddressMapper;
import com.ebock.mapper.UserMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class UserServiceIT {

    @InjectMock
    Keycloak keycloak;
    @InjectMock
    UserMapper userMapper;
    @InjectMock
    AddressMapper addressMapper;
    @InjectMock
    KeycloakAdapter keycloakAdapter;

    private RealmResource realmResource;
    private UsersResource usersResource;
    private UserResource userResource;

    @BeforeEach
    void setUp() {
        realmResource = mock(RealmResource.class);
        usersResource = mock(UsersResource.class);
        userResource = mock(UserResource.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

    @Test
    void testEndpoints_ProtectedBySecurity_Returns401() {
        // Assert authentification necessary
        given()
                .contentType(ContentType.JSON)
                .body(new UserEditPayload())
                .when()
                .put("/user/dubw5596/profil")
                .then()
                .statusCode(401);

        given()
                .contentType(ContentType.JSON)
                .body(new UserChangePasswordPayload())
                .when()
                .put("/user/dubw5596/security")
                .then()
                .statusCode(401);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/user/dubw5596/profile")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "dubw5596", roles = {"user"})
    void testChangePassword_Success() {
        UserChangePasswordPayload payload = new UserChangePasswordPayload();
        payload.oldPassword = "correctOldPassword";
        payload.newPassword = "secureNewPassword123";

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("keycloak-uuid-999");

        doNothing().when(keycloakAdapter).verifyOldPassword("dubw5596", "correctOldPassword");
        when(keycloakAdapter.getUserByCip("dubw5596")).thenReturn(userRep);
        doNothing().when(keycloakAdapter).resetPassword("keycloak-uuid-999", "secureNewPassword123");

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/user/dubw5596/security")
                .then()
                .statusCode(204);

        verify(keycloakAdapter, times(1)).resetPassword("keycloak-uuid-999", "secureNewPassword123");
    }

    @Test
    @TestSecurity(user = "dubw5596", roles = {"user"})
    void testEditProfile_WithExistingAddress_Returns200() {
        UserEditPayload payload = new UserEditPayload();
        payload.firstName = "William";
        payload.lastName = "Dubuc";

        AddressPayload addressPayload = new AddressPayload();
        addressPayload.street = "Sommet de Orford";
        addressPayload.civicNumber = 1;
        addressPayload.apptNumber = 1;
        addressPayload.provinceCode = "QC";
        addressPayload.country = "Québec";
        payload.address = addressPayload;

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("keycloak-uuid-999");
        userRep.setFirstName("OldFirst");
        userRep.setLastName("OldLast");
        userRep.setUsername("dubw5596");

        // Mock the adapter
        when(keycloakAdapter.getUserByCip("dubw5596")).thenReturn(userRep);

        User mockUserDb = new User();
        mockUserDb.addressId = 42;
        when(userMapper.getUserInfo("dubw5596")).thenReturn(mockUserDb);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/user/dubw5596/profil")
                .then()
                .statusCode(200);

        verify(addressMapper, times(1)).update(any());
        verify(keycloakAdapter, times(1)).updateUser(any(UserRepresentation.class));
    }

    @Test
    @TestSecurity(user = "hacker99", roles = {"user"})
    void testGetProfile_MismatchedCip_Returns403() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/user/dubw5596/profil")
                .then()
                .statusCode(403);

        verify(userMapper, never()).getUserInfo(anyString());
    }

    @Test
    @TestSecurity(user = "dubw5596", roles = {"user"})
    void testGetProfile_Success_Returns200() {
        User mockUserDb = new User();
        mockUserDb.addressId = 42;
        when(userMapper.getUserInfo("dubw5596")).thenReturn(mockUserDb);

        com.ebock.business.Address mockAddressDb = new com.ebock.business.Address();
        when(addressMapper.getAddressById(42)).thenReturn(mockAddressDb);

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/user/dubw5596/profil")
                .then()
                .statusCode(200)
                .body("user", org.hamcrest.Matchers.notNullValue())
                .body("address", org.hamcrest.Matchers.notNullValue());
    }
}