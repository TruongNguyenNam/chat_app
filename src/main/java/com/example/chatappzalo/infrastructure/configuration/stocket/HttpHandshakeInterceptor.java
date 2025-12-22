//package com.example.chatappzalo.infrastructure.configuration.stocket;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import java.util.Enumeration;
//import java.util.Map;
//
//@Component
//@Slf4j
//public class HttpHandshakeInterceptor implements HandshakeInterceptor {
//
//    private static final String AUTH_TOKEN_KEY = "auth_token"; // Key duy nhất
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//
//        log.info(">>> WebSocket Handshake: URI = {}", request.getURI());
//
//        if (request instanceof ServletServerHttpRequest servletRequest) {
//            HttpServletRequest httpReq = servletRequest.getServletRequest();
//
//            // Log headers để debug (giữ lại)
//            log.info("Handshake headers:");
//            Enumeration<String> headerNames = httpReq.getHeaderNames();
//            while (headerNames != null && headerNames.hasMoreElements()) {
//                String name = headerNames.nextElement();
//                log.info("  {}: {}", name, httpReq.getHeader(name));
//            }
//
//            String token = null;
//
//            // 1. Query param ?token=
//            String queryToken = httpReq.getParameter("token");
//            if (queryToken != null && !queryToken.isBlank()) {
//                token = queryToken.trim();
//                log.info("Token from query param: {}", hideToken(token));
//            }
//
//            // 2. Authorization header
//            if (token == null) {
//                String authHeader = httpReq.getHeader("Authorization");
//                if (authHeader != null && !authHeader.isBlank()) {
//                    token = authHeader.trim();
//                    log.info("Token from Authorization header");
//                }
//            }
//
//            // 3. X-Auth-Token header
//            if (token == null) {
//                String xToken = httpReq.getHeader("X-Auth-Token");
//                if (xToken != null && !xToken.isBlank()) {
//                    token = xToken.trim();
//                    log.info("Token from X-Auth-Token header");
//                }
//            }
//
//            // Lưu vào attributes với KEY DUY NHẤT
//            if (token != null) {
//                String finalToken = token.startsWith("Bearer ") ? token : "Bearer " + token;
//                attributes.put(AUTH_TOKEN_KEY, finalToken);
//                log.info("Handshake SUCCESS: Token saved to session attributes as 'auth_token'");
//            } else {
//                log.warn("Handshake: No token found in query param or headers");
//                // Vẫn cho phép handshake, auth sẽ fail ở CONNECT frame
//            }
//        }
//
//        return true; // Luôn cho phép handshake, auth ở STOMP CONNECT
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                               WebSocketHandler wsHandler, Exception ex) {
//        // no-op
//    }
//
//    private String hideToken(String token) {
//        if (token == null || token.length() < 10) return token;
//        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
//    }
//
//}
