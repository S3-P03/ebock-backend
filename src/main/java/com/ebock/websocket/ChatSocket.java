package com.ebock.websocket;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;

@WebSocket(path = "/chat/{room}")
public class ChatSocket {

    //Just a WebSocket endpoint for frontend

    @OnOpen
    public void onOpen(WebSocketConnection conn) {
    }
}
