package com.ebock.service;

import com.ebock.business.User;
import com.ebock.converter.UserConverter;
import com.ebock.dto.request.user.UserChangePasswordPayload;
import com.ebock.dto.response.user.SellerUserResponse;
import com.ebock.dto.response.user.UserResponse;
import com.ebock.mapper.UserMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
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
    @Inject
    Keycloak keycloak;

    @GET
    @Path("/me")
    @Authenticated
    public UserResponse me() {
        String cip = this.securityContext.getUserPrincipal().getName();
        String firstName = this.jwt.getClaim("given_name");
        String lastName = this.jwt.getClaim("family_name");
        String email = this.jwt.getClaim("email");
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

    @GET
    @Path("/{cip}/storefront")
    @PermitAll
    public SellerUserResponse cipStorefront(
            @PathParam("cip") String cip
    ) {
        if(userMapper.getUserCountByCip(cip) == 0)
            throw new NotFoundException("User not found");
        return userConverter.toSellerUserResponse(this.userMapper.getUserInfo(cip));
    }

    @PUT
    @Path("/changepassword")
    @Authenticated
    public void changeUserPassword(UserChangePasswordPayload payload) {
        String cip = this.securityContext.getUserPrincipal().getName();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(payload.newPassword);
        credential.setTemporary(false);

        List<UserRepresentation> users = keycloak.realm("ebock").users().search(cip, true);

        if (users.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        String userId = users.getFirst().getId();
        keycloak.realm("ebock")
                .users()
                .get(userId)
                .resetPassword(credential);
    }

    @PUT
    @Path("/edit")
    @Authenticated
    @Transactional
    public void edit(UserChangePasswordPayload payload) {
        String cip = this.securityContext.getUserPrincipal().getName();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(payload.newPassword);
        credential.setTemporary(false);

        List<UserRepresentation> users = keycloak.realm("ebock").users().search(cip, true);

        if (users.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        String userId = users.getFirst().getId();
        keycloak.realm("ebock")
                .users()
                .get(userId)
                .resetPassword(credential);
    }
}
