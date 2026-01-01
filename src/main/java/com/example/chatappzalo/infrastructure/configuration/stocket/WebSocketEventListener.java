package com.example.chatappzalo.infrastructure.configuration.stocket;


import com.example.chatappzalo.core.auth.payload.UserMsg;
import com.example.chatappzalo.core.chatapp.message.payload.ChatMessage;
import com.example.chatappzalo.entity.User;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.auth.AuthService;
import com.example.chatappzalo.service.auth.OnlineUserService;
import com.example.chatappzalo.service.auth.UserService;
import com.example.chatappzalo.service.auth.impl.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final IAuthService authService;
    private final OnlineUserService onlineUserService;
    private final UserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        // Lấy Principal (user đã authenticate)
        Principal principal = accessor.getUser();
        if (principal == null) {
            log.warn("Connect event without Principal: sessionId={}", accessor.getSessionId());
            return;
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.warn("User not found for username: {}", username);
            return;
        }

        Long userId = user.getId();

        // Lưu userId vào session để dùng khi disconnect (fallback)
        accessor.getSessionAttributes().put("userId", userId);

        // Thêm vào danh sách online in-memory (nếu chưa có)
        boolean added = onlineUserService.add(userId); // giả sử add() trả về true nếu thực sự thêm mới

        // Chỉ broadcast nếu đây là lần kết nối mới (tránh spam khi reconnect cùng tab)
        if (added) {
            // 1. Broadcast trạng thái online
            messagingTemplate.convertAndSend(
                    "/topic/user-status",
                    UserMsg.builder()
                            .userId(userId)
                            .fullName(user.getFullName())
                            .isOnline(true)
                            .lastActive(LocalDateTime.now())
                            .build()
            );

            // 2. Broadcast toàn bộ danh sách online users mới nhất cho TẤT CẢ client
            Set<Long> onlineUserIds = onlineUserService.getAllOnlineUserIds();
            messagingTemplate.convertAndSend("/topic/online-users", onlineUserIds);

            log.info("User connected: userId={}, username={}, total online: {}",
                    userId, username, onlineUserIds.size());
        } else {
            log.debug("User reconnected (already online): userId={}", userId);
        }

        // Cập nhật DB (nên async nếu nặng)
        authService.markOnline(userId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Long userId = null;
        User user = null;

        // Ưu tiên lấy từ Principal
        Principal principal = accessor.getUser();
        if (principal != null) {
            String username = principal.getName();
            user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                userId = user.getId();
            }
        }

        // Fallback: lấy từ session attributes (rất quan trọng với reconnect hoặc tab đóng đột ngột)
        if (userId == null) {
            Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
            if (sessionAttrs != null) {
                userId = (Long) sessionAttrs.get("userId");
                if (userId != null) {
                    user = userRepository.findById(userId).orElse(null);
                }
            }
        }

        // Không xác định được user → bỏ qua
        if (userId == null || user == null) {
            log.warn("Disconnect event without valid user: sessionId={}", accessor.getSessionId());
            return;
        }

        // Kiểm tra xem user có thực sự đang online không trước khi xóa
        boolean wasOnline = onlineUserService.isOnline(userId);

        if (wasOnline) {
            // Xóa khỏi danh sách online
            onlineUserService.remove(userId);

            // 1. Broadcast trạng thái offline
            messagingTemplate.convertAndSend(
                    "/topic/user-status",
                    UserMsg.builder()
                            .userId(userId)
                            .fullName(user.getFullName())
                            .isOnline(false)
                            .lastActive(LocalDateTime.now())
                            .build()
            );

            // 2. Broadcast danh sách online mới cho TẤT CẢ client
            Set<Long> onlineUserIds = onlineUserService.getAllOnlineUserIds();
            messagingTemplate.convertAndSend("/topic/online-users", onlineUserIds);

            log.info("User disconnected: userId={}, username={}, remaining online: {}",
                    userId, user.getUsername(), onlineUserIds.size());
        } else {
            log.debug("Ignored disconnect (user was not online): userId={}", userId);
        }

        // Cập nhật DB (offline)
        authService.markOffline(userId);
    }

//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        if (headerAccessor.getSessionAttributes() == null) {
//            log.warn("Session attributes is null during connect event");
//            return;
//        }
//
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//        String fullName = (String) headerAccessor.getSessionAttributes().get("fullName");
//        Number userIdNum = (Number) headerAccessor.getSessionAttributes().get("userId");
//        Long userId = userIdNum != null ? userIdNum.longValue() : null;
//
//        if (fullName != null) {
//            log.info("User connected: {} (userId: {})", fullName, userId);
//
//            ChatMessage chatMessage = ChatMessage.builder()
////                    .type(ChatMessage.MessageType.JOIN)
//                    .senderName(fullName)
//                    .build();
//
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        }
//    }


//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        if (headerAccessor.getSessionAttributes() == null) {
//            log.warn("Session attributes is null during disconnect event");
//            return;
//        }
//
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//        String fullName = (String) headerAccessor.getSessionAttributes().get("fullName");
//        if (fullName != null) {
//            log.info("User disconnected: {}", fullName);
//
//            ChatMessage chatMessage = ChatMessage.builder()
////                    .type(ChatMessage.MessageType.LEAVE)
//                    .senderName(fullName)
//                    .build();
//
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        }
//    }


    }

