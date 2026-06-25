package com.ebock.User;

import com.ebock.adapter.KeycloakAdapter;
import com.ebock.business.Address;
import com.ebock.business.User;
import com.ebock.converter.AddressConverter;
import com.ebock.converter.UserConverter;
import com.ebock.dto.request.user.AddressPayload;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock UserMapper userMapper;
    @Mock AddressMapper addressMapper;
    @Mock SecurityContext securityContext;
    @Mock JsonWebToken jwt;
    @Mock UserConverter userConverter;
    @Mock KeycloakAdapter keycloakAdapter;
    @Mock AddressConverter addressConverter;

    @InjectMocks
    UserService userService;

    // Notice: @BeforeEach and @AfterEach are gone. We don't need MockedStatic anymore.

    @Test
    void changeUserPassword_Success() {
        UserChangePasswordPayload payload = new UserChangePasswordPayload();
        payload.oldPassword = "oldPassword123";
        payload.newPassword = "newPassword123";

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("abcde123");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("uuid-12345");

        // Mock the adapter behavior
        doNothing().when(keycloakAdapter).verifyOldPassword("abcde123", "oldPassword123");
        when(keycloakAdapter.getUserByCip("abcde123")).thenReturn(userRep);
        doNothing().when(keycloakAdapter).resetPassword("uuid-12345", "newPassword123");

        Response response = userService.changeUserPassword(payload);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(keycloakAdapter, times(1)).resetPassword("uuid-12345", "newPassword123");
    }

    @Test
    void changeUserPassword_WrongOldPassword_ThrowsBadRequest() {
        UserChangePasswordPayload payload = new UserChangePasswordPayload();
        payload.oldPassword = "wrongPassword";

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("abcde123");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        // Simulate the adapter throwing the exception
        doThrow(new BadRequestException("Invalid old password"))
                .when(keycloakAdapter).verifyOldPassword("abcde123", "wrongPassword");

        assertThrows(BadRequestException.class, () -> userService.changeUserPassword(payload));

        // Ensure we never try to fetch the user or reset the password if verification fails
        verify(keycloakAdapter, never()).getUserByCip(anyString());
        verify(keycloakAdapter, never()).resetPassword(anyString(), anyString());
    }

    @Test
    void edit_AddressUpdate_ExistingAddress() {
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

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("dubw5596");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("uuid-123");
        userRep.setFirstName("OldWilli");
        userRep.setLastName("OldDubuc");
        userRep.setUsername("dubw5596");

        when(keycloakAdapter.getUserByCip("dubw5596")).thenReturn(userRep);

        when(addressConverter.toBusiness(any())).thenReturn(new Address());

        User userDb = new User();
        userDb.addressId = 99;
        when(userMapper.getUserInfo("dubw5596")).thenReturn(userDb);

        Response response = userService.editProfile(payload);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(addressMapper, times(1)).update(any(Address.class));
        verify(addressMapper, never()).insert(any(Address.class));
        verify(userMapper, never()).updateUserAddress(anyString(), anyInt());
        verify(keycloakAdapter, times(1)).updateUser(userRep);
    }

    @Test
    void edit_AddressInsertion_NonExistingAddress() {
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

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("dubw5596");
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("uuid-123");
        userRep.setFirstName("OldWilli");
        userRep.setLastName("OldDubuc");
        userRep.setUsername("dubw5596");

        when(keycloakAdapter.getUserByCip("dubw5596")).thenReturn(userRep);

        User userDb = new User();
        userDb.addressId = null;
        when(userMapper.getUserInfo("dubw5596")).thenReturn(userDb);

        when(addressConverter.toBusiness(any())).thenReturn(new Address());

        Response response = userService.editProfile(payload);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(addressMapper, times(1)).insert(any(Address.class));
        verify(userMapper, times(1)).updateUserAddress(eq("dubw5596"), anyInt());
        verify(addressMapper, never()).update(any(Address.class));
        verify(keycloakAdapter, times(1)).updateUser(userRep);
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
        when(userMapper.getUserCountByCip("dubw5596")).thenReturn(0);

        assertThrows(NotFoundException.class, () -> userService.cipStorefront("dubw5596"));
    }

    @Test
    void getProfile_Success() {
        // Arrange
        String cip = "dubw5596";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(cip);
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        User mockUser = new User();
        mockUser.addressId = 42;
        when(userMapper.getUserInfo(cip)).thenReturn(mockUser);

        Address mockAddress = new Address();
        when(addressMapper.getAddressById(42)).thenReturn(mockAddress);

        com.ebock.dto.response.user.ProfileUserResponse userResp = new com.ebock.dto.response.user.ProfileUserResponse();
        com.ebock.dto.response.user.ProfileAddressResponse addrResp = new com.ebock.dto.response.user.ProfileAddressResponse();

        when(userConverter.toProfileUserResponse(mockUser)).thenReturn(userResp);
        when(addressConverter.toProfileAddressResponse(mockAddress)).thenReturn(addrResp);

        // Act
        com.ebock.dto.response.user.ProfileResponse result = userService.getProfile();

        // Assert
        assertNotNull(result);
        assertEquals(userResp, result.user);
        assertEquals(addrResp, result.address);

        verify(userMapper, times(1)).getUserInfo(cip);
        verify(addressMapper, times(1)).getAddressById(42);
    }

    @Test
    void getProfile_UserNotFound_ThrowsNotFound() {
        // Arrange
        String cip = "dubw5596";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(cip);
        when(securityContext.getUserPrincipal()).thenReturn(principal);

        // L'utilisateur n'existe pas dans la base de données
        when(userMapper.getUserInfo(cip)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getProfile());
    }
}