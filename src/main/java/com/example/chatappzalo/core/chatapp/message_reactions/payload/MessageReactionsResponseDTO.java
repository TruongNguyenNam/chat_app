package com.example.chatappzalo.core.chatapp.message_reactions.payload;

import com.example.chatappzalo.entity.Message;
import com.example.chatappzalo.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactionsResponseDTO {


    private Long messageId;

    private List<CountMessageReactionsDTO> reactions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CountMessageReactionsDTO {
        private Long MessageReactionId;
        private String reaction;       // icon/sticker, ví dụ: "❤️"
        private Long totalReactions;   // tổng số reaction
    }


}
