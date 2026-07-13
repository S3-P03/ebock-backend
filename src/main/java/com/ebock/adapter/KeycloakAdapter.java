package com.ebock.adapter;

import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.regex.Pattern;

import static com.ebock.utils.UserUtils.isCipValid;


@ApplicationScoped
public class KeycloakAdapter {

    @Inject
    Keycloak keycloak;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.server-url")
    String serverUrl;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    String realm;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.client-secret")
    String clientSecret;
    
    /**
     * Verifies the user's current password by attempting to fetch a token.
     */
    public void verifyOldPassword(String username, String password) {
        try (Keycloak verificationClient = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build()) {

            verificationClient.tokenManager().getAccessToken();

        } catch (ClientWebApplicationException e) {
            throw new UnauthorizedException("Invalid old password");
        } catch (Exception e) {
            throw new InternalServerErrorException("Error while reaching IAM provider");
        }
    }

    /**
     * Fetch user by cip
     * @param cip of the user
     * @return User
     */
    public UserRepresentation getUserByCip(String cip) {
        List<UserRepresentation> users = keycloak.realm(realm).users().searchByUsername(cip, true);
        if (users == null || users.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return users.get(0);
    }

    /**
     * Fetch all user
     * @return User
     */
    public List<UserRepresentation> getAllUsers() {
        List<UserRepresentation> users = keycloak.realm(realm).users().list();

        return users;
    }

    /**
     * Enable a user
     * @param cip of the user
     */
    public void enableUser(String cip) {
        if (!isCipValid(cip)) {
            throw new BadRequestException("Invalid cip");
        }

        UserRepresentation userRepresentation = getUserByCip(cip);

        try {
            // Get the user
            UserResource userResource = keycloak.realm(realm).users().get(userRepresentation.getId());
            UserRepresentation user = userResource.toRepresentation();

            if(isUserAdmin(userResource)){
                throw new ForbiddenException("Cannot enable an admin");
            }

            // Change the status
            if (user.isEnabled()) {
                return;
            }
            user.setEnabled(true);

            // Save
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }

    /**
     * Disable a user
     * @param cip of the user
     */
    public void disableUser(String cip) {
        if (!isCipValid(cip)) {
            throw new BadRequestException("Invalid cip");
        }

        UserRepresentation userRepresentation = getUserByCip(cip);

        try {
            // Get the user
            UserResource userResource = keycloak.realm(realm).users().get(userRepresentation.getId());
            UserRepresentation user = userResource.toRepresentation();

            if(isUserAdmin(userResource)){
                throw new ForbiddenException("Cannot disable an admin");
            }

            // Change the status
            if (!user.isEnabled()) {
                return;
            }
            user.setEnabled(false);

            // Save
            userResource.update(user);
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }

    /**
     * Check if a user is enbaled
     * @param cip of the user
     */
    public boolean isUserEnabled(String cip) {
        if (!isCipValid(cip)) {
            throw new BadRequestException("Invalid cip");
        }

        UserRepresentation userRepresentation = getUserByCip(cip);

        try {
            // Get the user
            UserResource userResource = keycloak.realm(realm).users().get(userRepresentation.getId());
            UserRepresentation user = userResource.toRepresentation();

            return user.isEnabled();
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }

    /**
     * Check if a user is admin
     * @param userResource user
     */
    public boolean isUserAdmin(UserResource userResource) {
        return userResource.roles().realmLevel().listAll().stream().anyMatch(role -> "admin".equals(role.getName()));
    }

    /**
     * Reset the password of the user
     * @param userId of the user
     * @param newPassword
     */
    public void resetPassword(String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        keycloak.realm(realm).users().get(userId).resetPassword(credential);
    }

    /**
     * Add the dark role to a user
     * @param cip to add
     */
    public void addDarkRoleToUser(String cip) {
        if (!isCipValid(cip)) {
            throw new BadRequestException("Invalid cip");
        }

        UserRepresentation userRepresentation = getUserByCip(cip);

        try {
            // Get the user
            UserResource userResource = keycloak.realm(realm).users().get(userRepresentation.getId());
            RoleRepresentation role = keycloak.realm(realm).roles().get("dark").toRepresentation();

            // Add the role
            userResource.roles().realmLevel().add(List.of(role));
        } catch (NotFoundException e) {
            throw new NotFoundException("User or role not found");
        }
    }


    /**
     * Remove the dark role to a user
     * @param cip to remove
     */
    public void removeDarkRoleToUser(String cip) {
        if (!isCipValid(cip)) {
            throw new BadRequestException("Invalid cip");
        }

        UserRepresentation userRepresentation = getUserByCip(cip);

        try {
            // Get the user
            UserResource userResource = keycloak.realm(realm).users().get(userRepresentation.getId());
            RoleRepresentation role = keycloak.realm(realm).roles().get("dark").toRepresentation();

            // Remove the role
            userResource.roles().realmLevel().remove(List.of(role));
        } catch (NotFoundException e) {
            throw new NotFoundException("User or role not found");
        }
    }

    /**
     * Update a user
     * @param user to update
     */
    public void updateUser(UserRepresentation user) {
        keycloak.realm(realm).users().get(user.getId()).update(user);
    }
}
