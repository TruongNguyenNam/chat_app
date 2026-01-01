package com.example.chatappzalo.service.chat;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatRequestDTO;
import com.example.chatappzalo.core.chatapp.chat.payload.ChatResponseDTO;

import java.util.List;

public interface ChatService {

    List<ChatResponseDTO> getAllByChatType();

    ChatResponseDTO findByChatId(Long chatId);

    void createChatGroup(ChatRequestDTO chatRequestDTO);

    void markChatAsRead(Long chatId);  // đọc tin nhắn

}
