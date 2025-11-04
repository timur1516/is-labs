package ru.itmo.se.is.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import ru.itmo.se.is.websocket.WebSocketMessageType;

@ApplicationScoped
public class NotificationService {

    @Inject
    private WebSocketService webSocketService;

    public void notifyAll(WebSocketMessageType type) {
        String message = Json.createObjectBuilder()
                .add("type", type.name())
                .build().toString();

        webSocketService.broadcast(message);
    }
}
