package com.ebock.User;

import com.ebock.business.Address;
import com.ebock.business.User;
import com.ebock.converter.UserConverter;
import com.ebock.dto.request.user.UserChangePasswordPayload;
import com.ebock.dto.request.user.UserEditPayload;
import com.ebock.dto.response.user.SellerUserResponse;
import com.ebock.dto.response.user.UserResponse;
import com.ebock.mapper.AddressMapper;
import com.ebock.mapper.UserMapper;
import com.ebock.service.UserService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock UserMapper userMapper;
    @Mock AddressMapper addressMapper;
    @Mock SecurityContext securityContext;
    @Mock Keycloak keycloak;
    @Mock JsonWebToken jwt;
    @Mock RealmResource realmResource;
    @Mock UsersResource usersResource;
    @Mock UserResource userResource;
    @Mock UserConverter userConverter;

    @InjectMocks
    UserService userService;

    private MockedStatic<KeycloakBuilder> mockedBuilder;

    @BeforeEach
    void setUp() {
        mockedBuilder = Mockito.mockStatic(KeycloakBuilder.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockedBuilder.close();
    }

    @Test
    void changeUserPassword_Success() {
        UserChangePasswordPayload payload = new UserChangePasswordPayload();
        payload.oldPassword = "oldPassword123";
        payload.newPassword = "newPassword123";

        // Localized Security Setup
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("abcde123");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        // Localized Keycloak client mock chain
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        // Mock Keycloak Verification Client Builder Chain
        KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
        Keycloak verificationClientMock = mock(Keycloak.class);
        TokenManager tokenManagerMock = mock(TokenManager.class);

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

        // Mock Find User Chain
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("uuid-12345");
        when(usersResource.searchByUsername("abcde123", true)).thenReturn(List.of(userRep));
        when(usersResource.get("uuid-12345")).thenReturn(userResource);

        Response response = userService.changeUserPassword(payload);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(userResource).resetPassword(any());
    }

    @Test
    void changeUserPassword_WrongOldPassword_ThrowsBadRequest() {
        // Arrange
        UserChangePasswordPayload payload = new UserChangePasswordPayload();
        payload.oldPassword = "wrongPassword";

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("abcde123");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
        mockedBuilder.when(KeycloakBuilder::builder).thenReturn(builderMock);
        when(builderMock.serverUrl(any())).thenReturn(builderMock);
        when(builderMock.realm(any())).thenReturn(builderMock);
        when(builderMock.grantType(any())).thenReturn(builderMock);
        when(builderMock.clientId(any())).thenReturn(builderMock);
        when(builderMock.clientSecret(any())).thenReturn(builderMock);
        when(builderMock.username(any())).thenReturn(builderMock);
        when(builderMock.password(any())).thenReturn(builderMock);

        Keycloak verificationClientMock = mock(Keycloak.class);
        TokenManager tokenManagerMock = mock(TokenManager.class);

        when(builderMock.build()).thenReturn(verificationClientMock);
        when(verificationClientMock.tokenManager()).thenReturn(tokenManagerMock);
        when(tokenManagerMock.getAccessToken()).thenThrow(new jakarta.ws.rs.NotAuthorizedException("Unauthorized"));

        // Act and assert
        assertThrows(BadRequestException.class, () -> userService.changeUserPassword(payload));
    }

    @Test
    void edit_AddressUpdate_ExistingAddress() {
        UserEditPayload payload = new UserEditPayload();
        payload.newFirstName = "William";
        payload.newLastName = "Dubuc";
        payload.newStreet = "Sommet de Orford";
        payload.newCivicNumber = 12;
        payload.newApptNumber = 10;
        payload.newCountry = "Canada";
        payload.newProvinceCode = "QC";

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("dubw5596");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("uuid-123");
        userRep.setFirstName("OldWilli");
        userRep.setLastName("OldDubuc");
        userRep.setUsername("dubw5596");

        when(usersResource.searchByUsername("dubw5596", true)).thenReturn(List.of(userRep));
        when(usersResource.get("uuid-123")).thenReturn(userResource);

        User userDb = new User();
        userDb.addressId = 99;
        when(userMapper.getUserInfo("dubw5596")).thenReturn(userDb);

        Response response = userService.edit(payload);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(addressMapper, times(1)).update(any());
        verify(addressMapper, never()).insert(any());
        verify(userMapper, never()).updateUserAddress(any(), anyInt());
    }

    @Test
    void edit_AddressInsertion_NonExistingAddress() {
        UserEditPayload payload = new UserEditPayload();
        payload.newFirstName = "William";
        payload.newLastName = "Dubuc";
        payload.newStreet = "Sommet de Orford";
        payload.newCivicNumber = 12;
        payload.newApptNumber = 10;
        payload.newCountry = "Canada";
        payload.newProvinceCode = "QC";

        Address address = new Address();
        address.addressId = 1;

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("dubw5596");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("uuid-123");
        userRep.setFirstName("OldWilli");
        userRep.setLastName("OldDubuc");
        userRep.setUsername("dubw5596");

        when(usersResource.searchByUsername("dubw5596", true)).thenReturn(List.of(userRep));
        when(usersResource.get("uuid-123")).thenReturn(userResource);

        User userDb = new User();
        userDb.addressId = null;
        when(userMapper.getUserInfo("dubw5596")).thenReturn(userDb);

        Response response = userService.edit(payload);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(addressMapper, times(1)).insert(any());
        verify(userMapper, times(1)).updateUserAddress(eq("dubw5596"), anyInt());
        verify(addressMapper, never()).update(any());
    }

    @Test
    void testMeCreatesUserIfNotExists() {
        when(jwt.getClaim("given_name")).thenReturn("Jeef");
        when(jwt.getClaim("family_name")).thenReturn("Larouche");
        when(jwt.getClaim("email")).thenReturn("larj4236@usherbrooke.ca");

        Principal principal = () -> "larj4236";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(userMapper.getUserInfo("larj4236")).thenReturn(null);
        when(userConverter.toResponse(any())).thenReturn(new UserResponse());

        userService.me();

        verify(userMapper).createUser("larj4236", "larj4236@usherbrooke.ca", "Jeef", "Larouche");
    }

    @Test
    void testMeUpdatesUserIfInfoChanged() {
        when(jwt.getClaim("given_name")).thenReturn("Jeef");
        when(jwt.getClaim("family_name")).thenReturn("Larouche");
        when(jwt.getClaim("email")).thenReturn("larj4236@usherbrooke.ca");

        Principal principal = () -> "larj4236";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        User existingUser = new User();
        existingUser.firstName = "AutreUser";
        existingUser.lastName = "AutreNom";
        existingUser.email = "AutreMail@usherbrooke.ca";

        when(userMapper.getUserInfo("larj4236")).thenReturn(existingUser);
        when(userConverter.toResponse(any())).thenReturn(new UserResponse());

        userService.me();

        verify(userMapper).updateUser("larj4236", "larj4236@usherbrooke.ca", "Jeef", "Larouche");
    }

    @Test
    void testMeDoesNotUpdateIfUnchanged() {
        when(jwt.getClaim("given_name")).thenReturn("Jeef");
        when(jwt.getClaim("family_name")).thenReturn("Larouche");
        when(jwt.getClaim("email")).thenReturn("larj4236@usherbrooke.ca");

        Principal principal = () -> "larj4236";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        User existingUser = new User();
        existingUser.firstName = "Jeef";
        existingUser.lastName = "Larouche";
        existingUser.email = "larj4236@usherbrooke.ca";

        when(userMapper.getUserInfo("larj4236")).thenReturn(existingUser);
        when(userConverter.toResponse(any())).thenReturn(new UserResponse());

        userService.me();

        verify(userMapper, never()).updateUser(any(), any(), any(), any());
        verify(userMapper, never()).createUser(any(), any(), any(), any());
    }

    @Test
    void testMeReturnsUserResponse() {
        User existingUser = new User();
        when(jwt.getClaim("given_name")).thenReturn("Jeef");
        when(jwt.getClaim("family_name")).thenReturn("Larouche");
        when(jwt.getClaim("email")).thenReturn("larj4236@usherbrooke.ca");

        Principal principal = () -> "larj4236";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        existingUser.firstName = "Jeef";
        existingUser.lastName = "Larouche";
        existingUser.email = "larj4236@usherbrooke.ca";

        UserResponse expected = new UserResponse();
        when(userMapper.getUserInfo("larj4236")).thenReturn(existingUser);
        when(userConverter.toResponse(existingUser)).thenReturn(expected);

        UserResponse result = userService.me();

        assertEquals(expected, result);
    }

    @Test
    void testCipStorefrontReturnsCorrectUser() {
        User user = new User();
        SellerUserResponse expected = new SellerUserResponse();
        when(userMapper.getUserCountByCip("larj4236")).thenReturn(1);
        when(userMapper.getUserInfo("larj4236")).thenReturn(user);
        when(userConverter.toSellerUserResponse(user)).thenReturn(expected);

        SellerUserResponse result = userService.cipStorefront("larj4236");

        assertEquals(expected, result);
    }

    @Test
    void testCipStorefrontUnknownCipThrowsNotFound() {
        when(userMapper.getUserCountByCip("abcd1234")).thenReturn(0);

        assertThrows(NotFoundException.class, () -> userService.cipStorefront("abcd1234"));
    }
}