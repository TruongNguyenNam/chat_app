package com.example.chatappzalo.service.message;

import com.example.chatappzalo.core.chatapp.message.payload.MessageRequestDTO;
import com.example.chatappzalo.core.chatapp.message.payload.MessageResponseDTO;
import com.example.chatappzalo.entity.Message;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MessageService {

    void sendMessage(MessageRequestDTO request, MultipartFile[] files) throws IOException;

    List<MessageResponseDTO> findByChatId(Long chatId);
     void markMessagesAsRead(Long userId, Long chatId);

}
