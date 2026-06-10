package com.ebock.service;

import com.ebock.business.User;
import com.ebock.converter.UserConverter;
import com.ebock.dto.response.user.SellerUserResponse;
import com.ebock.dto.response.user.UserResponse;
import com.ebock.mapper.UserMapper;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserMapper userMapper;

    @Mock
    UserConverter userConverter;

    @Mock
    JsonWebToken jwt;

    @Mock
    SecurityContext securityContext;

    @InjectMocks
    UserService userService;

    @Test
    void testMeCreatesUserIfNotExists() {
        // arrange
        when(jwt.getClaim("given_name")).thenReturn("Jeef");
        when(jwt.getClaim("family_name")).thenReturn("Larouche");
        when(jwt.getClaim("email")).thenReturn("larj4236@usherbrooke.ca");

        Principal principal = () -> "larj4236";
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(userMapper.getUserInfo("larj4236")).thenReturn(null);
        when(userConverter.toResponse(any())).thenReturn(new UserResponse());

        // act
        userService.me();

        // assert
        verify(userMapper).createUser("larj4236", "larj4236@usherbrooke.ca", "Jeef", "Larouche");
    }

    @Test
    void testMeUpdatesUserIfInfoChanged() {
        // arrange
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

        // act
        userService.me();

        // assert
        verify(userMapper).updateUser("larj4236", "larj4236@usherbrooke.ca", "Jeef", "Larouche");
    }

    @Test
    void testMeDoesNotUpdateIfUnchanged() {
        // arrange
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

        // act
        userService.me();

        // assert
        verify(userMapper, never()).updateUser(any(), any(), any(), any());
        verify(userMapper, never()).createUser(any(), any(), any(), any());
    }

    @Test
    void testMeReturnsUserResponse() {
        // arrange
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

        // act
        UserResponse result = userService.me();

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testCipStorefrontReturnsCorrectUser() {
        // arrange
        User user = new User();
        SellerUserResponse expected = new SellerUserResponse();
        when(userMapper.findUserByCip("larj4236")).thenReturn(1);
        when(userMapper.getUserInfo("larj4236")).thenReturn(user);
        when(userConverter.toSellerUserResponse(user)).thenReturn(expected);

        // act
        SellerUserResponse result = userService.cipStorefront("larj4236");

        // assert
        assertEquals(expected, result);
    }

    @Test
    void testCipStorefrontUnknownCipThrowsNotFound() {
        // arrange
        when(userMapper.findUserByCip("abcd1234")).thenReturn(0);

        // act and assert
        assertThrows(NotFoundException.class, () -> userService.cipStorefront("abcd1234"));
    }

}
