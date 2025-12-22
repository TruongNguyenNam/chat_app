package com.example.chatappzalo.service.auth;

import com.example.chatappzalo.core.auth.payload.TokenDTO;
import com.example.chatappzalo.entity.Token;
import com.example.chatappzalo.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;


public interface JWTTokenService {
    String generateJWT(String username);

    Authentication parseTokenToUserInformation(String token);

    Authentication parseTokenToUserInformation(HttpServletRequest request);

    Claims parseClaims(String token);

    Token generateRefreshToken(User user);

    Boolean isRefreshTokenValid(String refreshToken);

    TokenDTO getNewToken(String refreshToken);
}
