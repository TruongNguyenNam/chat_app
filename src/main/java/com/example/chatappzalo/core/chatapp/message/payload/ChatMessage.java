package com.example.chatappzalo.core.chatapp.message.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private Long messageId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long chatId;
    private String content;
    private String messageType;      // TEXT, IMAGE,...
    private LocalDateTime sentAt;
    private List<String> mediaUrls = new ArrayList<>();
    private Long parentMessageId;
}
