package com.ebock.websocket;

import com.ebock.dto.response.message.ConsumedToken;
import com.ebock.dto.response.message.WsTokenResponse;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WsTokenServiceTest {

    @Mock
    SecurityIdentity identity;

    @Mock
    AccessTokenCredential accessTokenCredential;

    @InjectMocks
    WsTokenService wsTokenService;

    private static final String ENV_EBOCK = "ebock";
    private static final String ENV_DARK_EBOCK = "dark_ebock";

    @Test
    void testIssueTokenReturnsTokenForAuthenticatedUser() {
        // arrange
        when(identity.getCredential(AccessTokenCredential.class)).thenReturn(accessTokenCredential);
        when(accessTokenCredential.getToken()).thenReturn("real-access-token");

        // act
        Response response = wsTokenService.issueToken(ENV_EBOCK);

        // assert
        assertEquals(200, response.getStatus());
        WsTokenResponse body = (WsTokenResponse) response.getEntity();
        assertNotNull(body.token());
    }

    @Test
    void testIssueTokenDefaultsToEbockWhenEnvironmentMissing() {
        // arrange
        when(identity.getCredential(AccessTokenCredential.class)).thenReturn(accessTokenCredential);
        when(accessTokenCredential.getToken()).thenReturn("real-access-token");

        // act
        Response response = wsTokenService.issueToken(null);
        WsTokenResponse body = (WsTokenResponse) response.getEntity();
        ConsumedToken result = wsTokenService.consume(body.token());

        // assert
        assertEquals(200, response.getStatus());
        assertNotNull(result);
        assertEquals(ENV_EBOCK, result.environment());
    }

    @Test
    void testIssueTokenDefaultsToEbockWhenEnvironmentInvalid() {
        // arrange
        when(identity.getCredential(AccessTokenCredential.class)).thenReturn(accessTokenCredential);
        when(accessTokenCredential.getToken()).thenReturn("real-access-token");

        // act
        Response response = wsTokenService.issueToken("not-a-real-env");
        WsTokenResponse body = (WsTokenResponse) response.getEntity();
        ConsumedToken result = wsTokenService.consume(body.token());

        // assert
        assertEquals(200, response.getStatus());
        assertNotNull(result);
        assertEquals(ENV_EBOCK, result.environment());
    }

    @Test
    void testIssueTokenStoresAccessTokenAndEnvironmentRetrievableByConsume() {
        // arrange
        when(identity.getCredential(AccessTokenCredential.class)).thenReturn(accessTokenCredential);
        when(accessTokenCredential.getToken()).thenReturn("real-access-token");

        // act
        Response response = wsTokenService.issueToken(ENV_DARK_EBOCK);
        WsTokenResponse body = (WsTokenResponse) response.getEntity();
        ConsumedToken result = wsTokenService.consume(body.token());

        // assert
        assertNotNull(result);
        assertEquals("real-access-token", result.accessToken());
        assertEquals(ENV_DARK_EBOCK, result.environment());
    }

    @Test
    void testIssueReturnsNonNullUniqueToken() {
        // act
        String token1 = wsTokenService.issue("token-a", ENV_EBOCK);
        String token2 = wsTokenService.issue("token-b", ENV_EBOCK);

        // assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    void testConsumeReturnsAccessTokenAndEnvironmentForValidToken() {
        // arrange
        String token = wsTokenService.issue("my-access-token", ENV_EBOCK);

        // act
        ConsumedToken result = wsTokenService.consume(token);

        // assert
        assertNotNull(result);
        assertEquals("my-access-token", result.accessToken());
        assertEquals(ENV_EBOCK, result.environment());
    }

    @Test
    void testConsumeIsSingleUse() {
        // arrange
        String token = wsTokenService.issue("my-access-token", ENV_EBOCK);

        // act
        wsTokenService.consume(token); // first use
        ConsumedToken secondAttempt = wsTokenService.consume(token); // reuse

        // assert
        assertNull(secondAttempt);
    }

    @Test
    void testConsumeReturnsNullForUnknownToken() {
        // act
        ConsumedToken result = wsTokenService.consume("does-not-exist");

        // assert
        assertNull(result);
    }

    @Test
    void testConsumeReturnsNullAfterExpiry() throws Exception {
        // arrange
        String token = wsTokenService.issue("my-access-token", ENV_EBOCK);
        forceEntryToBeExpired(wsTokenService, token);

        // act
        ConsumedToken result = wsTokenService.consume(token);

        // assert
        assertNull(result);
    }

    @SuppressWarnings("unchecked")
    private void forceEntryToBeExpired(WsTokenService service, String token) throws Exception {
        Field tokensField = WsTokenService.class.getDeclaredField("tokens");
        tokensField.setAccessible(true);
        Map<String, Object> tokens = (Map<String, Object>) tokensField.get(service);

        Class<?> entryClass = Class.forName("com.ebock.websocket.WsTokenService$Entry");
        var entryConstructor = entryClass.getDeclaredConstructor(String.class, String.class, Instant.class);
        entryConstructor.setAccessible(true);
        Object expiredEntry = entryConstructor.newInstance("my-access-token", ENV_EBOCK, Instant.now().minusSeconds(1));

        tokens.put(token, expiredEntry);
    }
}