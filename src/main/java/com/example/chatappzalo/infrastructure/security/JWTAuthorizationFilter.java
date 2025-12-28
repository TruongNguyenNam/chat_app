package com.example.chatappzalo.infrastructure.security;




import com.example.chatappzalo.service.auth.impl.IJWTTokenServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {


    private final IJWTTokenServiceImpl ijwtTokenService;

    @Value("${jwt.token.authorization}")
    private String authorizationHeader;

    @Value("${jwt.token.prefix}")
    private String tokenPrefix;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. BỎ QUA TOÀN BỘ WEBSOCKET + SOCKJS
        if (path.startsWith("/ws/") || path.equals("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Bỏ qua auth endpoint
        if (path.startsWith("/api/v1/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Bỏ qua static resources (favicon, css, js, images...)
        if (path.startsWith("/favicon.ico") ||
                path.startsWith("/static/") ||
                path.startsWith("/assets/") ||
                path.matches(".+\\.(css|js|png|jpg|jpeg|gif|ico|woff2|woff|ttf)$")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ============== Từ đây mới xử lý JWT ==============
        String header = request.getHeader(authorizationHeader);

        if (header == null || !header.startsWith(tokenPrefix + " ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring((tokenPrefix + " ").length()).trim();
            Authentication auth = ijwtTokenService.parseTokenToUserInformation(token);

            if (auth != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.warn("JWT invalid for request {}: {}", path, e.getMessage());
            // Không throw, không response 401 → để controller tự xử lý
        }

        filterChain.doFilter(request, response);
    }
}


