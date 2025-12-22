package com.example.chatappzalo.core.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginInfoDto {
    private Long userId;
    private String username;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String email;
    private String token;
    private String refreshToken;
    private String role;
    private String gender;
    private String avatarUrl;

}
