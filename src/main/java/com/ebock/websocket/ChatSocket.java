package com.ebock.websocket;

import com.ebock.dto.response.message.RoomDetailsResponse;
import com.ebock.infrastructure.config.SchemaContextHolder;
import com.ebock.mapper.MessageMapper;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import java.util.Objects;


@WebSocket(path = "/chat/{room}")
public class ChatSocket {

    public static final UserData.TypedKey<String> ENV_KEY = UserData.TypedKey.forString("env");

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
        String env = this.identity.getAttribute("env");
        String room = conn.pathParam("room");

        if (env == null) {
            reject(conn);
            return;
        }

        SchemaContextHolder.setEnvironment(env);
        try {
            RoomDetailsResponse roomDetailsResponse = messageMapper.getRoomInformation(Integer.parseInt(room));

            if (roomDetailsResponse == null || (!Objects.equals(roomDetailsResponse.sellerCip, cip) && !Objects.equals(roomDetailsResponse.buyerCip, cip))) {
                reject(conn);
                return;
            }

            conn.userData().put(ENV_KEY, env);
        } finally {
            SchemaContextHolder.clear();
        }
    }

    private void reject(WebSocketConnection conn) {
        conn.close(new CloseReason(1008, "Forbidden: You are not allowed to connect to this room")).subscribe().with(
                success -> {},
                failure -> {}
        );
    }
}
