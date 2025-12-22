package com.example.chatappzalo.core.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String message;
    private String role;
    private String phoneNumber;
    private String gender;
    private boolean isActive;


}
