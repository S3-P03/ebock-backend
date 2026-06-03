package com.ebock.service;

import com.ebock.business.User;
import com.ebock.converter.UserConverter;
import com.ebock.dto.response.user.UserResponse;
import com.ebock.mapper.UserMapper;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Objects;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {
    @Inject
    UserMapper userMapper;
    @Context
    SecurityContext securityContext;
    @Inject
    UserConverter userConverter;
    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/me")
    @Authenticated
    public UserResponse me() {
        String cip = this.securityContext.getUserPrincipal().getName();
        String firstName = (String)this.jwt.getClaim("given_name");
        String lastName = (String)this.jwt.getClaim("family_name");
        String email = (String)this.jwt.getClaim("email");
        User user = this.userMapper.getUserInfo(cip);

        if (user==null){
            // The user doesn't exist
            userMapper.createUser(cip, email, firstName, lastName);
            user = this.userMapper.getUserInfo(cip);
        } else if (hasChanged(user, email, firstName, lastName)) {
            // Met à jour le nom de l'utilisateur
            userMapper.updateUser(cip, email, firstName, lastName);
            user.firstName = firstName;
            user.lastName = lastName;
            user.email = email;
        }

        return userConverter.toResponse(user);
    }

    private boolean hasChanged(User user, String email, String firstName, String lastName) {
        return !Objects.equals(user.firstName, firstName)
                || !Objects.equals(user.lastName, lastName)
                || !Objects.equals(user.email, email);
    }
}
