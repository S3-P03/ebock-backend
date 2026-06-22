package com.ebock.websocket;

import com.ebock.dto.response.message.MessageResponse;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MessageBroadcaster {

    @Inject
    OpenConnections connections;

    public void broadcast(MessageResponse message) {
        connections.forEach(conn -> conn.sendTextAndAwait(message));
    }
}