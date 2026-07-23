package com.ebock.websocket;

import com.ebock.dto.response.message.ConsumedToken;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
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

        ConsumedToken consumedToken = tokenService.consume(ticket);
        if (consumedToken == null) {
            return Uni.createFrom().nullItem();
        }

        TokenAuthenticationRequest request = new TokenAuthenticationRequest(new AccessTokenCredential(consumedToken.accessToken()));
        HttpSecurityUtils.setRoutingContextAttribute(request, context);

        return identityProviderManager.authenticate(request)
                .map(identity -> QuarkusSecurityIdentity.builder(identity)
                        .addAttribute("env", consumedToken.environment())
                        .build());
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