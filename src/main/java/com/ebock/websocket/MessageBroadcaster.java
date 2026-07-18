package com.ebock.websocket;

import com.ebock.dto.response.message.MessageResponse;
import io.quarkus.websockets.next.OpenConnections;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MessageBroadcaster {

    @Inject
    OpenConnections connections;

    public void broadcast(String room, MessageResponse message) {
        connections.stream()
                .filter(conn -> room.equals(conn.pathParam("room")))
                .forEach(conn -> conn.sendTextAndAwait(message));
    }
}