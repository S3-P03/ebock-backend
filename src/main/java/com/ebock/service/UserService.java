package com.ebock.service;

import com.ebock.adapter.KeycloakAdapter;
import com.ebock.business.Address;
import com.ebock.business.User;
import com.ebock.converter.AddressConverter;
import com.ebock.converter.UserConverter;
import com.ebock.dto.request.user.UserChangePasswordPayload;
import com.ebock.dto.request.user.EditPayload;
import com.ebock.dto.response.user.ListUtilisateursResponse;
import com.ebock.dto.response.user.ProfileResponse;
import com.ebock.dto.response.user.SellerUserResponse;
import com.ebock.dto.response.user.UserResponse;
import com.ebock.mapper.AddressMapper;
import com.ebock.mapper.UserMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Objects;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserService {
    @Inject
    UserMapper userMapper;
    @Inject
    AddressMapper addressMapper;
    @Context
    SecurityContext securityContext;
    @Inject
    UserConverter userConverter;
    @Inject
    JsonWebToken jwt;
    @Inject
    Keycloak keycloak;
    @Inject
    KeycloakAdapter keycloakAdapter;
    @Inject
    AddressConverter addressConverter;

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
            user = this.userMapper.getUserInfo(cip);
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

    @GET
    @Path("/profile")
    @Authenticated
    public ProfileResponse getProfile() {
        String cip = this.securityContext.getUserPrincipal().getName();

        User user = userMapper.getUserInfo(cip);

        if(user==null) throw new NotFoundException("User not found");

        Address address = addressMapper.getAddressById(user.addressId);

        ProfileResponse response = new ProfileResponse();
        response.user = userConverter.toProfileUserResponse(user);
        response.address = addressConverter.toProfileAddressResponse(address);

        return response;
    }

    @PUT
    @Path("/security")
    @Authenticated
    public Response changeUserPassword(UserChangePasswordPayload payload) {
        String cip = this.securityContext.getUserPrincipal().getName();

        // Verify the old password
        keycloakAdapter.verifyOldPassword(cip, payload.oldPassword);

        // Fetch the user
        String userId = keycloakAdapter.getUserByCip(cip).getId();

        // Execute the change
        keycloakAdapter.resetPassword(userId, payload.newPassword);

        return Response.noContent().build();
    }

    @PUT
    @Path("/profile")
    @Authenticated
    @Transactional
    public Response editProfile(EditPayload payload) {
        // Get the user
        String cip = this.securityContext.getUserPrincipal().getName();

        // Get user representation
        UserRepresentation user = keycloakAdapter.getUserByCip(cip);

        // Modify first and last name
        boolean isModified = applyNameChanges(user, payload);

        // Update address
        User userDb = userMapper.getUserInfo(cip);

        Address newAddress = addressConverter.toBusiness(payload.address);

        if (newAddress != null && userDb.addressId == null) {
            addressMapper.insert(newAddress);
            userMapper.updateUserAddress(cip, newAddress.addressId);
        } else if (newAddress != null) {
            newAddress.addressId = userDb.addressId;
            addressMapper.update(newAddress);
        }

        if (isModified) {
            userMapper.updateUser(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
            keycloakAdapter.updateUser(user);
        }

        return Response.ok().build();
    }

    @GET
    @Path("/list/")
    @RolesAllowed("admin")
    public ListUtilisateursResponse listUser() {
        // Fetch all users
        List<UserRepresentation> userRepresentations = keycloakAdapter.getAllUsers();

        // Convert to response
        ListUtilisateursResponse response = new ListUtilisateursResponse();
        response.utilisateurs = userConverter.toResponseFromUserRepresentation(userRepresentations);

        return response;
    }

    @PUT
    @Path("/enable/{cip}")
    @RolesAllowed("admin")
    public Response enableUser(@PathParam("cip") String cip) {
        // Enable the user
        keycloakAdapter.enableUser(cip);

        return Response.ok().build();
    }

    @PUT
    @Path("/disable/{cip}")
    @RolesAllowed("admin")
    public Response disableUser(@PathParam("cip") String cip) {
        // Disable the user
        keycloakAdapter.disableUser(cip);

        return Response.ok().build();
    }

    /**
     * Change the names of the user if needed
     * @param user to verify
     * @param payload containing the new names
     * @return if a change was made
     */
    private boolean applyNameChanges(UserRepresentation user, EditPayload payload) {
        boolean isModified = false;

        if (payload.user.firstName != null && !payload.user.firstName.isBlank()
                && !Objects.equals(user.getFirstName(), payload.user.firstName)) {
            user.setFirstName(payload.user.firstName.trim());
            isModified = true;
        }

        if (payload.user.lastName != null && !payload.user.lastName.isBlank()
                && !Objects.equals(user.getLastName(), payload.user.lastName)) {
            user.setLastName(payload.user.lastName.trim());
            isModified = true;
        }

        return isModified;
    }
}
