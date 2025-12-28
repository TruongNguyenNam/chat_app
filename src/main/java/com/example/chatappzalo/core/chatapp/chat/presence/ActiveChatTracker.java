package com.example.chatappzalo.core.chatapp.chat.presence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class ActiveChatTracker {
    private final ConcurrentMap<Long, Long> userActiveChat = new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, Set<Long>> chatActiveUsers = new ConcurrentHashMap<>();

    public void enterChat(Long userId, Long chatId) {
        if (userId == null) {
            log.warn("enterChat called with null userId");
            return;
        }
        if (chatId == null) {
            log.warn("enterChat called with null chatId for userId={}", userId);
            return;
        }

        // Rời chat cũ nếu có
        Long oldChatId = userActiveChat.put(userId, chatId);
        if (oldChatId != null && !oldChatId.equals(chatId)) {
            removeUserFromChat(oldChatId, userId);
        }

        // Thêm vào chat mới - an toàn với null đã được kiểm tra ở trên
        chatActiveUsers
                .computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet())
                .add(userId);

        log.debug("User {} entered chat {}", userId, chatId);
    }

    public void leaveChat(Long userId, Long chatId) {
        if (userId == null || chatId == null) {
            return;
        }

        if (chatId.equals(userActiveChat.get(userId))) {
            userActiveChat.remove(userId);
            removeUserFromChat(chatId, userId);
            log.debug("User {} left chat {}", userId, chatId);
        }
    }

    private void removeUserFromChat(Long chatId, Long userId) {
        if (chatId == null || userId == null) {
            return;
        }

        Set<Long> users = chatActiveUsers.get(chatId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                chatActiveUsers.remove(chatId);
            }
        }
    }

    public void onDisconnect(Long userId) {
        if (userId == null) {
            return;
        }

        Long chatId = userActiveChat.remove(userId);
        if (chatId != null) {
            removeUserFromChat(chatId, userId);
            log.debug("User {} disconnected, removed from chat {}", userId, chatId);
        }
    }

    public Set<Long> getActiveUsersInChat(Long chatId) {
        if (chatId == null) {
            return Collections.emptySet();
        }
        return chatActiveUsers.getOrDefault(chatId, Collections.emptySet());
    }

}
