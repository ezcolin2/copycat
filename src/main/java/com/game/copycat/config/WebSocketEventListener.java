package com.game.copycat.config;

import com.game.copycat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final RoomService roomService;


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Map<String, Object> sessionAttributes = StompHeaderAccessor
                .wrap(event.getMessage())
                .getSessionAttributes();
        for (Map.Entry<String, Object> entry : sessionAttributes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}