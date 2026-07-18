package com.ebock.websocket;

import com.ebock.dto.response.message.RoomDetailsResponse;
import com.ebock.mapper.MessageMapper;
import io.quarkus.oidc.AuthenticationContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.websockets.next.CloseReason;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import java.util.Objects;


@WebSocket(path = "/chat/{room}")
public class ChatSocket {

    @Inject
    SecurityIdentity identity;

    @Inject
    MessageMapper messageMapper;

    @OnOpen
    public void onOpen(WebSocketConnection conn) {
        if (identity.isAnonymous()) {
            reject(conn);
            return;
        }

        String cip = this.identity.getPrincipal().getName();
        String room = conn.pathParam("room");

        RoomDetailsResponse roomDetailsResponse = messageMapper.getRoomInformation(Integer.parseInt(room));

        if (roomDetailsResponse == null || (!Objects.equals(roomDetailsResponse.sellerCip, cip) && ! Objects.equals(roomDetailsResponse.buyerCip, cip))) {
            reject(conn);
        }
    }

    private void reject(WebSocketConnection conn) {
        conn.close(new CloseReason(1008, "Forbidden: You are not allowed to connect to this room")).subscribe().with(
                success -> {},
                failure -> {}
        );
    }
}
