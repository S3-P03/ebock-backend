package com.ebock.User;

import com.ebock.business.User;
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
                .put("/user/edit")
                .then()
                .statusCode(401);

        given()
                .contentType(ContentType.JSON)
                .body(new UserChangePasswordPayload())
                .when()
                .put("/user/changepassword")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "dubw5596", roles = {"user"})
    void testChangePassword_Success() {
        UserChangePasswordPayload payload = new UserChangePasswordPayload();
        payload.oldPassword = "correctOldPassword";
        payload.newPassword = "secureNewPassword123";

        // mock everything in the mock builder
        try (MockedStatic<KeycloakBuilder> mockedBuilder = Mockito.mockStatic(KeycloakBuilder.class)) {
            KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
            Keycloak verificationClientMock = mock(Keycloak.class);
            TokenManager tokenManagerMock = mock(TokenManager.class);
            AccessTokenResponse tokenResponseMock = mock(org.keycloak.representations.AccessTokenResponse.class);

            mockedBuilder.when(KeycloakBuilder::builder).thenReturn(builderMock);

            when(builderMock.serverUrl(any())).thenReturn(builderMock);
            when(builderMock.realm(any())).thenReturn(builderMock);
            when(builderMock.grantType(any())).thenReturn(builderMock);
            when(builderMock.clientId(any())).thenReturn(builderMock);
            when(builderMock.clientSecret(any())).thenReturn(builderMock);
            when(builderMock.username(any())).thenReturn(builderMock);
            when(builderMock.password(any())).thenReturn(builderMock);
            when(builderMock.build()).thenReturn(verificationClientMock);
            when(verificationClientMock.tokenManager()).thenReturn(tokenManagerMock);
            when(tokenManagerMock.getAccessToken()).thenReturn(tokenResponseMock);

            // Mock accessing user from keycloak
            UserRepresentation userRep = new UserRepresentation();
            userRep.setId("keycloak-uuid-999");
            when(usersResource.searchByUsername("dubw5596", true)).thenReturn(List.of(userRep));
            when(usersResource.get("keycloak-uuid-999")).thenReturn(userResource);

            given()
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .put("/user/changepassword")
                    .then()
                    .statusCode(204);

            verify(userResource, times(1)).resetPassword(any());
        }
    }

    @Test
    @TestSecurity(user = "dubw5596", roles = {"user"})
    void testEditProfile_WithExistingAddress_Returns200() {
        UserEditPayload payload = new UserEditPayload();
        payload.newFirstName = "William";
        payload.newLastName = "Dubuc";
        payload.newStreet = "Sommet de Orford";

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("keycloak-uuid-999");
        userRep.setFirstName("OldFirst");
        userRep.setLastName("OldLast");
        userRep.setUsername("dubw5596");

        when(usersResource.searchByUsername("dubw5596", true)).thenReturn(List.of(userRep));
        when(usersResource.get("keycloak-uuid-999")).thenReturn(userResource);

        User mockUserDb = new User();
        mockUserDb.addressId = 42;
        when(userMapper.getUserInfo("dubw5596")).thenReturn(mockUserDb);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/user/edit")
                .then()
                .statusCode(200);

        verify(addressMapper, times(1)).update(any());
        verify(userResource, times(1)).update(any());
    }
}