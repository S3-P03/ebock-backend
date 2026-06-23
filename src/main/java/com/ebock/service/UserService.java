package com.ebock.service;

import com.ebock.business.User;
import com.ebock.converter.UserConverter;
import com.ebock.dto.request.user.UserChangePasswordPayload;
import com.ebock.dto.request.user.UserEditPayload;
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
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
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

        try (Keycloak userVerificationClient = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8180")
                .realm("ebock")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("ebock-backend")
                .clientSecret("devBackendSecret")
                .username(cip)
                .password(payload.oldPassword)
                .build()) {

            userVerificationClient.tokenManager().getAccessToken();

        } catch (NotAuthorizedException e) {
            throw new BadRequestException("Invalid old password provided.");
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to reach identity provider.");
        }

        List<UserRepresentation> users = keycloak.realm("ebock").users().searchByUsername(cip, true);

        if (users.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        String userId = users.getFirst().getId();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(payload.newPassword);
        credential.setTemporary(false);

        keycloak.realm("ebock")
                .users()
                .get(userId)
                .resetPassword(credential);
    }

    @PUT
    @Path("/edit")
    @Authenticated
    @Transactional
    public void edit(UserEditPayload payload) {
        // Get the user
        String cip = this.securityContext.getUserPrincipal().getName();
        List<UserRepresentation> users = keycloak.realm("ebock").users().searchByUsername(cip, true);

        if (users.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        UserRepresentation user = users.getFirst();

        // Modify first and last name
        boolean isModified = applyNameChanges(user, payload);

        if (isModified) {
            persistUser(user);
        }


    }

    /**
     * Change the names of the user if needed
     * @param user to verify
     * @param payload containing the new names
     * @return if a change was made
     */
    private boolean applyNameChanges(UserRepresentation user, UserEditPayload payload) {
        boolean isModified = false;

        if (payload.newFirstName != null && !payload.newFirstName.isBlank()
                && !Objects.equals(user.getFirstName(), payload.newFirstName)) {
            user.setFirstName(payload.newFirstName.trim());
            isModified = true;
        }

        if (payload.newLastName != null && !payload.newLastName.isBlank()
                && !Objects.equals(user.getLastName(), payload.newLastName)) {
            user.setLastName(payload.newLastName.trim());
            isModified = true;
        }

        return isModified;
    }

    /**
     * Save the user on keycloack and the db
     * @param user to save
     */
    private void persistUser(UserRepresentation user) {
        try {
            UserResource userResource = keycloak.realm("ebock").users().get(user.getId());
            userResource.update(user);

            userMapper.updateUser(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error updating the user in keycloack or local db", e);
        }
    }
}
