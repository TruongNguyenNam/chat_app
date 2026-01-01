package com.example.chatappzalo.core.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMsg {
    private Long userId;
    private String fullName;
    private boolean isOnline;
    private LocalDateTime lastActive;
    private LocalDateTime lastLogin;
}
