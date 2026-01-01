package com.example.chatappzalo.core.chatapp.chat.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatListUpdateMsg {

    private Long chatId;

    private Long lastMessageId;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;

    private Long senderId;
    private String senderName;

    private Integer unreadCount;

}
