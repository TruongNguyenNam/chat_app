package com.example.chatappzalo.service.auth.impl;


import com.example.chatappzalo.core.auth.payload.TokenDTO;
import com.example.chatappzalo.entity.Token;

import com.example.chatappzalo.entity.User;
import com.example.chatappzalo.infrastructure.security.UserPrincipal;
import com.example.chatappzalo.repositories.TokenRepository;
import com.example.chatappzalo.service.auth.JWTTokenService;
import com.example.chatappzalo.service.auth.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class IJWTTokenServiceImpl implements JWTTokenService {


    private final UserService service;
    private final TokenRepository tokenRepository;

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expiration.time}")
    private Long expirationTime;

    @Value("${jwt.token.refresh.expiration.time}")
    private Long REFRESH_EXPIRATION_TIME;


    private Key getSigningKey() {
        try {
            byte[] keyBytes = jwtSecret.getBytes("UTF-8");
            log.info("JWT Secret Key length: {} bytes", keyBytes.length);
            if (keyBytes.length < 64) {
                log.error("JWT secret key is too short: {} bytes, required at least 64 bytes for HS512", keyBytes.length);
                throw new IllegalArgumentException("JWT secret key must be at least 64 bytes for HS512");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error creating signing key: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create signing key", e);
        }
    }


    @Override
    @Transactional
    public String generateJWT(String username) {
        Date now = new Date();
        User user = service.getAccountByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng: " + username);
        }
        Claims claims = Jwts.claims().setSubject(username);
                                        // vì setSubjcet vào chính truc tiép vào đây nên mội thứ trong
                                        // Jwt khi bắn ra sẽ đều phải chạy trong Principal chỉ là đại diện

//        claims.put("userId", Math.toIntExact(user.getId()));
        claims.put("userId", user.getId());
        claims.put("role",user.getRole().name());

        claims.put("fullName",user.getFullName());
        log.info("Tạo token cho user: {}, userId: {}, thời gian: {}", username, user.getId(), now);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        log.info("Token: {}", token);
        return token;
    }


    @Override
    @Transactional
    public Authentication parseTokenToUserInformation(String token) {

        // 1️⃣ Token null hoặc rỗng → không xác thực được
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            // 2️⃣ Dùng secret key để verify + parse JWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())       // key ký token
                    .setAllowedClockSkewSeconds(60)       // cho phép lệch giờ 60s
                    .build()
                    .parseClaimsJws(token)                // verify chữ ký + parse
                    .getBody();                           // lấy payload (claims)

            // 3️⃣ Lấy dữ liệu đã put khi generateJWT
            String username = claims.getSubject();        // sub → username
            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);
            // thêm phần fullName mới mục đích sau này để bắn lên thông tin ui người sử dụng
            String fullName = claims.get("fullName", String.class);
            // 4️⃣ Thiếu dữ liệu quan trọng → token không hợp lệ
            if (username == null || userId == null || role == null) {
                return null;
            }

            // 5️⃣ Tạo Principal (đại diện cho user trong Spring Security)
            UserPrincipal principal = new UserPrincipal(
                    userId,                               // id user
                    username,// username (routing WS)
                    fullName,
                    AuthorityUtils.createAuthorityList(role) // ROLE_*
            );

            // 6️⃣ Gói Principal vào Authentication
            // Authentication = danh tính user + quyền
            return new UsernamePasswordAuthenticationToken(
                    principal,                            // 👈 sẽ thành Principal
                    null,                                 // không cần password
                    principal.getAuthorities()            // quyền
            );

        } catch (ExpiredJwtException e) {
            // 7️⃣ Token hết hạn
            log.warn("Token hết hạn");
        } catch (Exception e) {
            // 8️⃣ Token sai chữ ký / format
            log.warn("Token không hợp lệ: {}", e.getMessage());
        }

        // 9️⃣ Parse thất bại → không xác thực
        return null;
    }


//    @Override
//    @Transactional
//    public Authentication parseTokenToUserInformation(String token) {
//
//        if (token == null || token.isBlank()) {
//            return null;
//        }
//
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .setAllowedClockSkewSeconds(60)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String username = claims.getSubject();
//            Long userId = claims.get("userId", Long.class);
//            String role = claims.get("role", String.class);
//
//            if (username == null || userId == null || role == null) {
//                return null;
//            }
//
//            UserPrincipal principal = new UserPrincipal(
//                    userId,
//                    username,
//                    AuthorityUtils.createAuthorityList(role)
//            );
//
//            return new UsernamePasswordAuthenticationToken(
//                    principal,
//                    null,
//                    principal.getAuthorities()
//            );
//
//        } catch (ExpiredJwtException e) {
//            log.warn("Token hết hạn");
//        } catch (Exception e) {
//            log.warn("Token không hợp lệ: {}", e.getMessage());
//        }
//
//        return null;
//    }


//    @Override
//    @Transactional
//    public Authentication parseTokenToUserInformation(String token) {
//        if (token == null || token.isBlank()) {
//            return null;
//        }
//
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .setAllowedClockSkewSeconds(60)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String username = claims.getSubject();
//            if (username == null) return null;
//
//            User user = service.getAccountByUsername(username);
//            if (user == null) {
//                log.warn("User không tồn tại từ token: {}", username);
//                return null;
//            }
//
//            UserDetails userDetails = org.springframework.security.core.userdetails.User
//                    .withUsername(user.getUsername())
//                    .password(user.getPassword())
//                    .authorities(user.getRole().toString())
//                    .build();
//
//            UsernamePasswordAuthenticationToken auth =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//            auth.setDetails(claims); // để lấy userId = claims.get("userId")
//            return auth;
//
//        } catch (ExpiredJwtException e) {
//            log.warn("Token hết hạn: {}", e.getMessage());
//        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
//            log.warn("Token không hợp lệ: {}", e.getMessage());
//        } catch (SignatureException e) {
//            log.warn("Chữ ký JWT sai: {}", e.getMessage());
//        } catch (Exception e) {
//            log.error("Lỗi parse token: {}", e.getMessage());
//        }
//        return null;
//    }

    @Override
    @Transactional
    public Authentication parseTokenToUserInformation(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.substring(7);
        return parseTokenToUserInformation(token); // Gọi method chung
    }

    @Override
    @Transactional
    public Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is null or empty");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException | MalformedJwtException |
                 SignatureException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            throw e;
        }
    }

//    @Transactional
//    public Authentication parseTokenToUserInformationV2(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
//
//        if (token == null || !token.startsWith("Bearer ")) {
//            return null;
//        }
//
//        try {
//            String jwtToken = token.substring(7);
//
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .setAllowedClockSkewSeconds(60)
//                    .build()
//                    .parseClaimsJws(jwtToken)
//                    .getBody();
//
//            String username = claims.getSubject();
//            User user = service.getAccountByUsername(username);
//
//            if (user == null) {
//                return null;
//            }
//
//            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                    user.getUsername(),
//                    user.getPassword(),
//                    AuthorityUtils.createAuthorityList(user.getRole().toString())
//            );
//
//            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                    userDetails, null, userDetails.getAuthorities());
//            auth.setDetails(claims);
//            return auth;
//        } catch (Exception e) {
//            log.error("Lỗi khi phân tích JWT: {}", e.getMessage(), e);
//            return null;
//        }
//    }


    @Override
    @Transactional
    public Token generateRefreshToken(User user) {
        try {

// tạo ra RefreshToken dựa vào account
            // Tạo token mới
            Token refreshToken = new Token(
                    user,
                    UUID.randomUUID().toString(),
                    Token.TokenType.REFRESH_TOKEN,
                    new Date(new Date().getTime() + REFRESH_EXPIRATION_TIME)
            );

            // Xóa các token cũ của account
            tokenRepository.deleteByUser(user);

            // Lưu token mới và trả về
            return tokenRepository.save(refreshToken);
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có lỗi xảy ra
            e.printStackTrace();
            throw new RuntimeException("Không thể tạo token mới.");
        }
    }

    @Override
    @Transactional
    public Boolean isRefreshTokenValid(String refreshToken) {
        Token entity = tokenRepository.findByKeyAndType(refreshToken, Token.TokenType.REFRESH_TOKEN);
        if(entity == null || entity.getExpiredDate().before(new Date())){
            throw new IllegalArgumentException("Token không hợp lệ.");
        }
        return true;
    }

    @Override
    @Transactional
    public TokenDTO getNewToken(String refreshToken) {
        Token oldRefreshToken = tokenRepository.findByKeyAndType(refreshToken, Token.TokenType.REFRESH_TOKEN);

        if (oldRefreshToken == null || oldRefreshToken.getExpiredDate().before(new Date())) {
            throw new IllegalArgumentException("Refresh Token không hợp lệ hoặc đã hết hạn.");
        }

        // Xoá các token cũ
        tokenRepository.deleteByUser(oldRefreshToken.getUser());

        // Tạo ra RefreshToken mới
        Token newRefreshToken = generateRefreshToken(oldRefreshToken.getUser());

        // Tạo ra Token mới sau thời gian tạo ra
        String newToken = generateJWT(oldRefreshToken.getUser().getUsername());

        return new TokenDTO(newToken, newRefreshToken.getKey());
    }
}
