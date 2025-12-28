package com.example.chatappzalo.infrastructure.configuration.stocket;

import com.example.chatappzalo.infrastructure.security.UserPrincipal;
import com.example.chatappzalo.infrastructure.utils.StompPrincipal;
import com.example.chatappzalo.service.auth.JWTTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTTokenService jwtTokenService;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // hoặc cụ thể: "http://localhost:5173", "https://yourdomain.com"
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user"); // cho private message
    }



    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) return message;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new MessagingException("Missing Authorization header");
                    }

                    Authentication auth =
                            jwtTokenService.parseTokenToUserInformation(authHeader.substring(7));

                    UserPrincipal user = (UserPrincipal) auth.getPrincipal();

                    accessor.setUser(new StompPrincipal(user.getUsername()));

                    accessor.setSessionAttributes(new ConcurrentHashMap<>());
                    accessor.getSessionAttributes().put("userId", user.getId());
                    accessor.getSessionAttributes().put("username", user.getUsername());
                    accessor.getSessionAttributes().put("fullName", user.getFullName());

                    log.info("WS CONNECT: {}", user.getUsername());
                }

                return message;
            }
        });
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//                StompHeaderAccessor accessor =
//                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//                if (accessor == null) return message;
//
//
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//
//                    if (accessor.getSessionAttributes() == null) {
//                        accessor.setSessionAttributes(new ConcurrentHashMap<>());
//                    }
//
//                    String authHeader = accessor.getFirstNativeHeader("Authorization");
//
//                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                        throw new org.springframework.messaging.MessagingException(
//                                "Missing Authorization Bearer token"
//                        );
//                    }
//
//                    String token = authHeader.substring(7);
//                    Authentication auth = jwtTokenService.parseTokenToUserInformation(token);
//
//                    if (auth == null || !auth.isAuthenticated()) {
//                        throw new org.springframework.messaging.MessagingException(
//                                "Invalid or expired JWT token"
//                        );
//                    }
//
//                    log.info("Principal name (auth.getName()) = {}", auth.getName());
//
//                    accessor.setUser(auth);
//                    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
//
//                    accessor.getSessionAttributes().put("userId", principal.getId());
//                    accessor.getSessionAttributes().put("username", principal.getUsername());
//                    accessor.getSessionAttributes().put("fullName", principal.getFullName());
//                    log.info("WebSocket CONNECT OK – userId={}, username={}, fullName = {}",
//                            principal.getId(), principal.getUsername(), principal.getFullName());
//                }
//
//                return message;
//            }
//        });
//    }


//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//                StompHeaderAccessor accessor =
//                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//                if (accessor == null) {
//                    return message;
//                }
//
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//
//                    String authHeader = accessor.getFirstNativeHeader("Authorization");
//                    log.info("WebSocket CONNECT ← {}", authHeader != null ? "có token" : "KHÔNG CÓ TOKEN");
//
//                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                        log.warn("Từ chối kết nối – thiếu Bearer token");
//                        throw new org.springframework.messaging.MessagingException(
//                                "Missing Authorization Bearer token"
//                        );
//                    }
//
//                    String token = authHeader.substring(7);
//
//                    Authentication auth = jwtTokenService.parseTokenToUserInformation(token);
//                    if (auth == null || !auth.isAuthenticated()) {
//                        log.warn("Từ chối kết nối – token sai hoặc hết hạn");
//                        throw new org.springframework.messaging.MessagingException(
//                                "Invalid or expired JWT token"
//                        );
//                    }
//
//                    // ✅ Gán Principal cho WebSocket
//                    accessor.setUser(auth);
//
//                    // ✅ Parse claims ĐÚNG CÁCH
//                    Claims claims = jwtTokenService.parseClaims(token);
//                    Long userId = claims.get("userId", Long.class);
//
//                    accessor.getSessionAttributes().put("userId", userId);
//                    accessor.getSessionAttributes().put("username", auth.getName());
//
//                    log.info("WebSocket CONNECT THÀNH CÔNG – User: {} (ID:{})",
//                            auth.getName(), userId);
//                }
//
//                return message;
//            }
//        });
//    }



}
