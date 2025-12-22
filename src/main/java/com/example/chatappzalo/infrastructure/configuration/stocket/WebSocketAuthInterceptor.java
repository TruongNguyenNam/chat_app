//package com.example.chatappzalo.infrastructure.configuration.stocket;
//
//
//import com.example.chatappzalo.service.auth.JWTTokenService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//
//@RequiredArgsConstructor
//@Slf4j
//@Component
//public class WebSocketAuthInterceptor implements ChannelInterceptor {
//
//    private final JWTTokenService jwtTokenService;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            // Lấy username từ connectHeaders (cách client gửi)
//            String username = accessor.getFirstNativeHeader("username");
//
//            if (username != null && !username.isBlank()) {
//                log.info("✅ Lấy được username từ connectHeaders: {}", username);
//                // Lưu vào session attributes để EventListener dùng
//                accessor.getSessionAttributes().put("username", username);
//            } else {
//                log.warn("❌ Không có username trong connectHeaders!");
//            }
//        }
//
//        return message;
//    }
//
////    @Override
////    public Message<?> preSend(Message<?> message, MessageChannel channel) {
////        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
////        StompCommand command = accessor.getCommand();
////
////        if (StompCommand.CONNECT.equals(command)) {
////            log.info("\n================ 🔥 NEW WEBSOCKET CONNECT REQUEST 🔥 ================");
////
////            // Lấy Authorization header từ STOMP CONNECT frame
////            String authHeader = accessor.getFirstNativeHeader("Authorization");
////            log.info("➡ Authorization Header nhận được: {}", authHeader);
////
////            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
////                log.error("❌ Từ chối kết nối WebSocket: Thiếu hoặc sai định dạng Authorization header");
////                throw new SecurityException("Thiếu JWT token để kết nối WebSocket");
////            }
////
////            String token = authHeader.substring(7); // Bỏ "Bearer "
////
////            try {
////                // Parse token để lấy thông tin user
////                Authentication authentication = jwtTokenService.parseTokenToUserInformation(token);
////
////                if (authentication == null || !authentication.isAuthenticated()) {
////                    log.error("❌ Từ chối kết nối: Token không hợp lệ hoặc user không tồn tại");
////                    throw new SecurityException("Token không hợp lệ");
////                }
////
////                String username = authentication.getName();
////                Integer userId = (Integer) authentication.getDetails().get("userId"); // vì bạn put "userId" vào claims
////
////                log.info("✅ WebSocket CONNECT thành công! User: {} (ID: {})", username, userId);
////
////                // Lưu thông tin vào session attributes để EventListener dùng
////                accessor.getSessionAttributes().put("username", username);
////                accessor.getSessionAttributes().put("userId", userId);
////
////                // (Tùy chọn) Gán user vào Spring Security context cho WebSocket session
////                accessor.setUser(authentication);
////
////                log.info("==================== WEBSOCKET CONNECT ACCEPTED ====================\n");
////
////            } catch (Exception e) {
////                log.error("❌ Lỗi xác thực WebSocket: {}", e.getMessage());
////                throw new SecurityException("Xác thực thất bại: " + e.getMessage());
////            }
////        }
////
////        return message;
////    }
//
//    @Override
//    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
//            log.info("🔌 WebSocket DISCONNECTED - User: {}", accessor.getSessionAttributes().get("username"));
//            // Không cần clear SecurityContext vì mỗi session có context riêng
//        }
//    }
////    @Override
////    public Message<?> preSend(Message<?> message, MessageChannel channel) {
////        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
////        StompCommand command = accessor.getCommand();
////
////        if (StompCommand.CONNECT.equals(command)) {
////            log.info("\n================ 🔥 NEW WEBSOCKET CONNECT REQUEST 🔥 ================");
////
////            // 🔍 Log toàn bộ headers client gửi lên
////            log.info("Headers từ client gửi vào STOMP CONNECT: {}", accessor.toNativeHeaderMap());
////
////            String authHeader = accessor.getFirstNativeHeader("Authorization");
////            log.info("➡ Authorization Header BE nhận được = {}", authHeader);
////
////            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
////                log.error("❌ WebSocket CONNECT bị từ chối → Không có Authorization hợp lệ!");
////                return null;
////            }
////
////            String token = authHeader.substring(7);
////            log.info("🔑 JWT Token Extracted = {}", token);
////
////            try {
////                Authentication authentication = jwtTokenService.parseTokenToUserInformation(token);
////
////                if (authentication == null || !authentication.isAuthenticated()) {
////                    log.error("❌ WebSocket REJECT → Token không hợp lệ!");
////                    return null;
////                }
////
////                accessor.setUser(authentication);
////                SecurityContextHolder.getContext().setAuthentication(authentication);
////
////                log.info("🟢 WebSocket authenticated SUCCESS! → User: {}", authentication.getName());
////                log.info("==================== CONNECT ACCEPTED ====================\n");
////
////            } catch (Exception e) {
////                log.error("❌ Lỗi xác thực WebSocket: {}", e.getMessage());
////                return null;
////            }
////        }
////
////        return message;
////    }
//
////    @Override
////    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
////        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
////        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
////            log.warn("🔌 WebSocket DISCONNECTED — Clear SecurityContext!");
////            SecurityContextHolder.clearContext();
////        }
////    }
//    }
//
//
//
//
