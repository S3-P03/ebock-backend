package com.ebock.service;

import com.ebock.business.User;
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

import java.util.List;
import java.util.Map;

@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {
    @Inject
    UserMapper userMapper;
    @Context
    SecurityContext securityContext;
    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/me")
    @Authenticated
    public User me() {
        String cip = this.securityContext.getUserPrincipal().getName();

        return this.userMapper.getUserInfo(cip);
    }
}
