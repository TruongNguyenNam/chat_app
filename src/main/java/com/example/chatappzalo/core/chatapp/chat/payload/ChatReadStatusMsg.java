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
public class ChatReadStatusMsg {
    private Long chatId;
    private Long userId;            // người vừa đọc
    private Long lastReadMessageId;
    private LocalDateTime readAt;  // chính là trường sendAt trong message


}
