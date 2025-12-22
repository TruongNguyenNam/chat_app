package com.example.chatappzalo.service.chat;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatResponseDTO;

import java.util.List;

public interface ChatService {

    List<ChatResponseDTO> getAllByChatType();

    ChatResponseDTO findByChatId(Long chatId);



}
