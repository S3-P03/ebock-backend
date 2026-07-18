package com.ebock.websocket;

import com.ebock.dto.response.message.WsTokenResponse;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
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

    @Test
    void testIssueTokenReturnsTokenForAuthenticatedUser() {
        // arrange
        when(identity.getCredential(AccessTokenCredential.class)).thenReturn(accessTokenCredential);
        when(accessTokenCredential.getToken()).thenReturn("real-access-token");

        // act
        Response response = wsTokenService.issueToken();

        // assert
        assertEquals(200, response.getStatus());
        WsTokenResponse body = (WsTokenResponse) response.getEntity();
        assertNotNull(body.token());
    }

    @Test
    void testIssueTokenStoresAccessTokenRetrievableByConsume() {
        // arrange
        when(identity.getCredential(AccessTokenCredential.class)).thenReturn(accessTokenCredential);
        when(accessTokenCredential.getToken()).thenReturn("real-access-token");

        // act
        Response response = wsTokenService.issueToken();
        WsTokenResponse body = (WsTokenResponse) response.getEntity();
        String result = wsTokenService.consume(body.token());

        // assert
        assertEquals("real-access-token", result);
    }

    @Test
    void testIssueReturnsNonNullUniqueToken() {
        // act
        String token1 = wsTokenService.issue("token-a");
        String token2 = wsTokenService.issue("token-b");

        // assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    void testConsumeReturnsAccessTokenForValidToken() {
        // arrange
        String token = wsTokenService.issue("my-access-token");

        // act
        String result = wsTokenService.consume(token);

        // assert
        assertEquals("my-access-token", result);
    }

    @Test
    void testConsumeIsSingleUse() {
        // arrange
        String token = wsTokenService.issue("my-access-token");

        // act
        wsTokenService.consume(token); // first use
        String secondAttempt = wsTokenService.consume(token); // reuse

        // assert
        assertNull(secondAttempt);
    }

    @Test
    void testConsumeReturnsNullForUnknownToken() {
        // act
        String result = wsTokenService.consume("does-not-exist");

        // assert
        assertNull(result);
    }

    @Test
    void testConsumeReturnsNullAfterExpiry() throws Exception {
        // arrange
        String token = wsTokenService.issue("my-access-token");
        forceEntryToBeExpired(wsTokenService, token);

        // act
        String result = wsTokenService.consume(token);

        // assert
        assertNull(result);
    }

    @SuppressWarnings("unchecked")
    private void forceEntryToBeExpired(WsTokenService service, String token) throws Exception {
        Field tokensField = WsTokenService.class.getDeclaredField("tokens");
        tokensField.setAccessible(true);
        Map<String, Object> tokens = (Map<String, Object>) tokensField.get(service);

        Class<?> entryClass = Class.forName("com.ebock.websocket.WsTokenService$Entry");
        var entryConstructor = entryClass.getDeclaredConstructor(String.class, Instant.class);
        entryConstructor.setAccessible(true);
        Object expiredEntry = entryConstructor.newInstance("my-access-token", Instant.now().minusSeconds(1));

        tokens.put(token, expiredEntry);
    }
}