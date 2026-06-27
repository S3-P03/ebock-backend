package com.ebock.adapter;

import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;


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
            throw new NotFoundException("User not found in IAM");
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
     * Update a user
     * @param user to update
     */
    public void updateUser(UserRepresentation user) {
        keycloak.realm(realm).users().get(user.getId()).update(user);
    }
}
