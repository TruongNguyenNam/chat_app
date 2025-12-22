package com.example.chatappzalo.infrastructure.configuration.stocket;


import com.example.chatappzalo.core.chatapp.message.payload.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        if (headerAccessor.getSessionAttributes() == null) {
            log.warn("Session attributes is null during connect event");
            return;
        }

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        Number userIdNum = (Number) headerAccessor.getSessionAttributes().get("userId");
        Long userId = userIdNum != null ? userIdNum.longValue() : null;

        if (username != null) {
            log.info("User connected: {} (userId: {})", username, userId);

            ChatMessage chatMessage = ChatMessage.builder()
//                    .type(ChatMessage.MessageType.JOIN)
                    .senderName(username)
                    .build();

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        if (headerAccessor.getSessionAttributes() == null) {
            log.warn("Session attributes is null during disconnect event");
            return;
        }

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            log.info("User disconnected: {}", username);

            ChatMessage chatMessage = ChatMessage.builder()
//                    .type(ChatMessage.MessageType.LEAVE)
                    .senderName(username)
                    .build();

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }



}
