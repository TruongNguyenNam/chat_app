package com.example.chatappzalo.core.chatapp.user.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String token;
    private String refreshToken;
    private String role;
    private String gender;
    private String avatarUrl;

}
