package com.ebock.websocket;

import com.ebock.dto.response.message.RoomDetailsResponse;
import com.ebock.mapper.MessageMapper;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.websockets.next.CloseReason;
import io.quarkus.websockets.next.UserData;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatSocketTest {

    @Mock
    SecurityIdentity identity;

    @Mock
    MessageMapper messageMapper;

    @Mock
    WebSocketConnection connection;

    @Mock
    UserData userData;

    @Mock
    Principal principal;

    @InjectMocks
    ChatSocket chatSocket;

    private static final String ENV = "ebock";

    @Test
    void testOnOpenAllowsSellerToConnect() {
        // arrange
        String requestCip = "larj4236";
        RoomDetailsResponse room = new RoomDetailsResponse();
        room.sellerCip = requestCip;
        room.buyerCip = "someoneElse";

        when(identity.isAnonymous()).thenReturn(false);
        when(identity.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);
        when(identity.getAttribute("env")).thenReturn(ENV);
        when(connection.pathParam("room")).thenReturn("5");
        when(messageMapper.getRoomInformation(5)).thenReturn(room);
        when(connection.userData()).thenReturn(userData);

        // act
        chatSocket.onOpen(connection);

        // assert
        verify(connection, never()).close(any());
    }

    @Test
    void testOnOpenAllowsBuyerToConnect() {
        // arrange
        String requestCip = "larj4236";
        RoomDetailsResponse room = new RoomDetailsResponse();
        room.sellerCip = "someoneElse";
        room.buyerCip = requestCip;

        when(identity.isAnonymous()).thenReturn(false);
        when(identity.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(requestCip);
        when(identity.getAttribute("env")).thenReturn(ENV);
        when(connection.pathParam("room")).thenReturn("5");
        when(messageMapper.getRoomInformation(5)).thenReturn(room);
        when(connection.userData()).thenReturn(userData);

        // act
        chatSocket.onOpen(connection);

        // assert
        verify(connection, never()).close(any());
    }

    @Test
    void testOnOpenRejectsUnrelatedUser() {
        // arrange
        RoomDetailsResponse room = new RoomDetailsResponse();
        room.sellerCip = "sellerCip";
        room.buyerCip = "buyerCip";

        when(identity.isAnonymous()).thenReturn(false);
        when(identity.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("randomCip");
        when(identity.getAttribute("env")).thenReturn(ENV);
        when(connection.pathParam("room")).thenReturn("5");
        when(messageMapper.getRoomInformation(5)).thenReturn(room);
        when(connection.close(any())).thenReturn(Uni.createFrom().voidItem());

        // act
        chatSocket.onOpen(connection);

        // assert
        ArgumentCaptor<CloseReason> captor = ArgumentCaptor.forClass(CloseReason.class);
        verify(connection).close(captor.capture());
        assertEquals(1008, captor.getValue().getCode());
    }

    @Test
    void testOnOpenRejectsWhenRoomDoesNotExist() {
        // arrange
        when(identity.isAnonymous()).thenReturn(false);
        when(identity.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(identity.getAttribute("env")).thenReturn(ENV);
        when(connection.pathParam("room")).thenReturn("999");
        when(messageMapper.getRoomInformation(999)).thenReturn(null);
        when(connection.close(any())).thenReturn(Uni.createFrom().voidItem());

        // act
        chatSocket.onOpen(connection);

        // assert
        verify(connection).close(any());
    }

    @Test
    void testOnOpenRejectsAnonymousUser() {
        // arrange
        when(identity.isAnonymous()).thenReturn(true);
        when(connection.close(any())).thenReturn(Uni.createFrom().voidItem());

        // act
        chatSocket.onOpen(connection);

        // assert
        verify(connection).close(any());
        verifyNoInteractions(messageMapper);
    }

    @Test
    void testOnOpenRejectsWhenEnvMissing() {
        // arrange
        when(identity.isAnonymous()).thenReturn(false);
        when(identity.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("larj4236");
        when(identity.getAttribute("env")).thenReturn(null);
        when(connection.close(any())).thenReturn(Uni.createFrom().voidItem());

        // act
        chatSocket.onOpen(connection);

        // assert
        verify(connection).close(any());
        verifyNoInteractions(messageMapper);
    }
}