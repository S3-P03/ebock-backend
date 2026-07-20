package com.ebock.websocket;

import com.ebock.dto.response.message.WsTokenResponse;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Path("/ws-token")
public class WsTokenService {

    @Inject
    SecurityIdentity identity;

    private static final int TTL_SECONDS = 10;

    private record Entry(String accessToken, Instant expiresAt) {}

    private final Map<String, Entry> tokens = new ConcurrentHashMap<>();

    public String issue(String accessToken) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new Entry(accessToken, Instant.now().plusSeconds(TTL_SECONDS)));
        return token;
    }

    public String consume(String token) {
        Entry entry = tokens.remove(token);
        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            return null;
        }
        return entry.accessToken();
    }

    @POST
    @Authenticated
    public Response issueToken() {
        // HTTP call to generate token that will be passed as parameter to WebSocket
        String accessToken = identity.getCredential(AccessTokenCredential.class).getToken();
        String token = issue(accessToken);
        return Response.ok(new WsTokenResponse(token, TTL_SECONDS)).build();
    }
}
