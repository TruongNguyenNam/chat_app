//package com.example.chatappzalo.infrastructure.configuration.stocket;
//import com.example.chatappzalo.service.auth.JWTTokenService;
//import io.jsonwebtoken.Claims;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
//public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer{
//
//    private final JWTTokenService jwtTokenService;
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String authHeader = accessor.getFirstNativeHeader("Authorization");
//
//                    log.info("[WebSocket] Nhận CONNECT frame – Authorization: {}",
//                            authHeader != null ? "có token" : "KHÔNG có token");
//
//                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                        log.warn("[WebSocket] Thiếu Bearer token → từ chối kết nối");
//                        return null;
//                    }
//
//                    String token = authHeader.substring(7);
//                    Authentication auth = jwtTokenService.parseTokenToUserInformation(token);
//
//                    if (auth == null || !auth.isAuthenticated()) {
//                        log.warn("[WebSocket] Token không hợp lệ hoặc hết hạn → từ chối");
//                        return null;
//                    }
//
//                    // THÀNH CÔNG
//                    accessor.setUser(auth);
//                    accessor.getSessionAttributes().put("username", auth.getName());
//
//                    var claims = (io.jsonwebtoken.Claims) auth.getDetails();
//                    Integer userId = claims.get("userId", Integer.class);
//                    accessor.getSessionAttributes().put("userId", userId);
//
//                    log.info("[WebSocket] CONNECT THÀNH CÔNG – User: {} (ID: {})", auth.getName(), userId);
//                }
//                return message;
//            }
//        });
//    }
//}
