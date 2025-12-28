package com.example.chatappzalo.core.chatapp.chat.presence;

import com.example.chatappzalo.infrastructure.security.UserPrincipal;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatPresenceController {
    private final ActiveChatTracker activeChatTracker;

    @MessageMapping("/chat.enter")
    public void handleEnterChat(
            @Payload Map<String, Object> payload,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        if (headerAccessor.getSessionAttributes() == null) {
            log.warn("SessionAttributes is null in chat.enter");
            return;
        }

        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
        if (!(userIdObj instanceof Number userIdNum)) {
            log.warn("userId not found in sessionAttributes");
            return;
        }

        Long userId = userIdNum.longValue();

        Object chatIdObj = payload.get("chatId");
        if (!(chatIdObj instanceof Number chatIdNum)) {
            log.warn("Invalid chatId payload: {}", chatIdObj);
            return;
        }

        Long chatId = chatIdNum.longValue();

        activeChatTracker.enterChat(userId, chatId);

        log.debug("User {} entered chat {}", userId, chatId);
    }


    @MessageMapping("/chat.leave")
    public void handleLeaveChat(@Payload Map<String, Object> payload) {
        Long userId = SecurityUtils.getCurrentUserId();
        Object chatIdObj = payload.get("chatId");

        if (chatIdObj instanceof Number) {
            Long chatId = ((Number) chatIdObj).longValue();
            activeChatTracker.leaveChat(userId, chatId);
        }
    }

}
