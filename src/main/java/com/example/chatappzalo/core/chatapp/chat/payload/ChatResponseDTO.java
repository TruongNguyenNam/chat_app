package com.example.chatappzalo.core.chatapp.chat.payload;

import com.example.chatappzalo.entity.Chat;
import com.example.chatappzalo.entity.ChatMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponseDTO {

    private Long chatId;

    private String chatType;

    private String chatName;

    private String fullName;

    private String gender;

    private String avatarUrl;

    private Boolean isOnline;

    private LocalDateTime lastActive;

}
