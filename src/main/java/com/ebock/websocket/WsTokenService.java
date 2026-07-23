package com.ebock.websocket;

import com.ebock.dto.response.message.ConsumedToken;
import com.ebock.dto.response.message.WsTokenResponse;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Path("/ws-token")
public class WsTokenService {

    @Inject
    SecurityIdentity identity;

    private static final int TTL_SECONDS = 10;
    private static final Set<String> VALID_ENVIRONMENTS = Set.of("ebock", "dark_ebock");

    private record Entry(String accessToken, String environment, Instant expiresAt) {}

    private final Map<String, Entry> tokens = new ConcurrentHashMap<>();

    public String issue(String accessToken, String environment) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new Entry(accessToken, environment, Instant.now().plusSeconds(TTL_SECONDS)));
        return token;
    }

    public ConsumedToken consume(String token) {
        Entry entry = tokens.remove(token);
        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            return null;
        }
        return new ConsumedToken(entry.accessToken(), entry.environment());
    }

    @POST
    @Authenticated
    public Response issueToken(@HeaderParam("Environment") String environment) {
        // HTTP call to generate token that will be passed as parameter to WebSocket
        if (environment == null || !VALID_ENVIRONMENTS.contains(environment)) {
            environment = "ebock";
        }

        String accessToken = identity.getCredential(AccessTokenCredential.class).getToken();
        String token = issue(accessToken, environment);
        return Response.ok(new WsTokenResponse(token, TTL_SECONDS)).build();
    }
}
