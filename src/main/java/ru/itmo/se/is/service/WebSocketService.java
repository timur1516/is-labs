package ru.itmo.se.is.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ApplicationScoped
public class WebSocketService {
    private final Set<Session> sessions = new CopyOnWriteArraySet<>();

    public void register(Session session) {
        sessions.add(session);
    }

    public void unregister(Session session) {
        sessions.remove(session);
    }

    public void broadcast(String message) {
        sessions.forEach(session -> {
            safeSend(session, message);
        });
    }

    private void safeSend(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException exception) {
            unregister(session);
        }
    }
}
