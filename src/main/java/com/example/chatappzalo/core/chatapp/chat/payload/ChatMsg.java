package com.example.chatappzalo.core.chatapp.chat.payload;

import com.example.chatappzalo.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMsg {
    private Chat.ChatType chatType;     // GROUP_CREATED, NEW_MESSAGE, ...
    private Long chatId;
    private String chatName;
    private Long createdBy;
}
