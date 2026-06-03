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

@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {
    @Inject
    UserMapper userMapper;
    @Context
    SecurityContext securityContext;
    @Inject
    UserConverter userConverter;

    @GET
    @Path("/me")
    @Authenticated
    public UserResponse me() {
        String cip = this.securityContext.getUserPrincipal().getName();
        User user = this.userMapper.getUserInfo(cip);
        return userConverter.toResponse(user);
    }
}
