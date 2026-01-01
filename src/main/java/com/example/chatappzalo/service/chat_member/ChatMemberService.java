package com.example.chatappzalo.service.chat_member;

import com.example.chatappzalo.core.chatapp.chat_member.payload.ChatMemberResponseDTO;
import com.example.chatappzalo.entity.ChatMember;

import java.util.List;

public interface ChatMemberService {
    Long CountUnreadByMe();

    List<ChatMemberResponseDTO> findByChatId(Long chatId);


}
