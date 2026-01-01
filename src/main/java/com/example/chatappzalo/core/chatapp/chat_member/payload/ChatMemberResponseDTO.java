package com.example.chatappzalo.core.chatapp.chat_member.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMemberResponseDTO {

    private Long id;

    private String fullName;

    private String avatarUrl;

    private String phone;

    private String chatType;

    private String chatName;




}
