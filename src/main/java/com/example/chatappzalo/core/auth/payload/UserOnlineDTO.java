package com.example.chatappzalo.core.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivateKey;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOnlineDTO {
    private Long userId;
    private boolean isOnline;
    private LocalDateTime lastActive;

}
