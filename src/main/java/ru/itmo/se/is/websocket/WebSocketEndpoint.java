package ru.itmo.se.is.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import ru.itmo.se.is.service.WebSocketService;

@ServerEndpoint("/ws")
@ApplicationScoped
public class WebSocketEndpoint {
    @Inject
    private WebSocketService webSocketService;

    @OnOpen
    public void onOpen(Session session) {
        webSocketService.register(session);
    }

    @OnClose
    public void onClose(Session session) {
        webSocketService.unregister(session);
    }
}
