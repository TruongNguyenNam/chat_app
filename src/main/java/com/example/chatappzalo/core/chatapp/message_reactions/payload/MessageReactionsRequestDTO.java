package com.example.chatappzalo.core.chatapp.message_reactions.payload;

import com.example.chatappzalo.entity.Message;
import com.example.chatappzalo.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactionsRequestDTO {
    private String reaction;  //các sticker emoji

    private Long messageId;

}
