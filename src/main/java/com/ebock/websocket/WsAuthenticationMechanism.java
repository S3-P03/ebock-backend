package com.ebock.websocket;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.quarkus.vertx.http.runtime.security.HttpSecurityUtils;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String PARAM = "wsToken";

    private final WsTokenService tokenService;

    public WsAuthenticationMechanism(WsTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        String upgrade = context.request().getHeader("Upgrade");
        String ticket = context.queryParams().get(PARAM);

        if (ticket == null || !"websocket".equalsIgnoreCase(upgrade)) {
            return Uni.createFrom().nullItem();
        }

        String accessToken = tokenService.consume(ticket);
        if (accessToken == null) {
            return Uni.createFrom().nullItem();
        }

        TokenAuthenticationRequest request = new TokenAuthenticationRequest(new AccessTokenCredential(accessToken));
        HttpSecurityUtils.setRoutingContextAttribute(request, context);

        return identityProviderManager.authenticate(request);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context) {
        return Uni.createFrom().item(new HttpCredentialTransport(HttpCredentialTransport.Type.AUTHORIZATION, "bearer"));
    }

    @Override
    public int getPriority() {
        return HttpAuthenticationMechanism.DEFAULT_PRIORITY + 1;
    }
}