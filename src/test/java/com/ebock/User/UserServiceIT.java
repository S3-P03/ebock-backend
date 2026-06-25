package com.ebock.User;

import com.ebock.adapter.KeycloakAdapter;
import com.ebock.business.Address;
import com.ebock.business.User;
import com.ebock.converter.AddressConverter;
import com.ebock.converter.UserConverter;
import com.ebock.dto.request.user.AddressPayload;
import com.ebock.dto.request.user.UserChangePasswordPayload;
import com.ebock.dto.request.user.UserEditPayload;
import com.ebock.dto.response.user.ProfileAddressResponse;
import com.ebock.dto.response.user.ProfileUserResponse;
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
import static org.hamcrest.Matchers.notNullValue;
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
    @InjectMock
    UserConverter userConverter;
    @InjectMock
    AddressConverter addressConverter;

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
                .when()
                .get("/user/dubw1234/profil")
                .then()
                .statusCode(401);

        Mockito.verify(userMapper, Mockito.never()).getUserInfo(any());
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
        when(addressConverter.toBusiness(any())).thenReturn(new Address());

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
    @TestSecurity(user = "asdf1234")
    public void testProfile_Forbidden_MismatchedCip_ShouldReturn403() {
        given()
                .when()
                .get("/user/dubw1234/profil")
                .then()
                .statusCode(403);

        Mockito.verify(userMapper, Mockito.never()).getUserInfo(any());
    }

    @Test
    @TestSecurity(user = "dubw1234")
    public void testProfile_Success_ShouldReturn200AndProfileData() {
        // Arrange
        String cip = "dubw1234";
        int fakeAddressId = 42;

        User mockUser = new User();
        mockUser.addressId = fakeAddressId;

        Address mockAddress = new Address();

        ProfileUserResponse mockUserResp = new ProfileUserResponse();
        ProfileAddressResponse mockAddressResp = new ProfileAddressResponse();

        when(userMapper.getUserInfo(cip)).thenReturn(mockUser);
        when(addressMapper.getAddressById(fakeAddressId)).thenReturn(mockAddress);
        when(userConverter.toProfileUserResponse(mockUser)).thenReturn(mockUserResp);
        when(addressConverter.toProfileAddressResponse(mockAddress)).thenReturn(mockAddressResp);

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/user/" + cip + "/profil")
                .then()
                .statusCode(200)
                .body("user", notNullValue())
                .body("address", notNullValue());

        Mockito.verify(userMapper).getUserInfo(cip);
        Mockito.verify(addressMapper).getAddressById(fakeAddressId);
    }
}