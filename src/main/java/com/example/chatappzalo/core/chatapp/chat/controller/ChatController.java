package com.example.chatappzalo.core.chatapp.chat.controller;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatResponseDTO;
import com.example.chatappzalo.core.chatapp.contact.payload.ContactResponseDTO;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.chat.ChatService;
import com.example.chatappzalo.service.contact.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@Validated
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseData<List<ChatResponseDTO>> findAllByChatType() {
        List<ChatResponseDTO> chatResponseDTOS = chatService.getAllByChatType();

        return ResponseData.<List<ChatResponseDTO>>builder()
                .status(200)
                .message("danh sách đoạn chat của bạn thành công")
                .data(chatResponseDTOS)
                .build();
    }



    @GetMapping("/{chatId}")
    public ResponseData<ChatResponseDTO> findByChatId(@PathVariable(name = "chatId") Long chatId) {
        ChatResponseDTO chatResponseDTOS = chatService.findByChatId(chatId);

        return ResponseData.<ChatResponseDTO>builder()
                .status(200)
                .message("đã tìm thấy đoạn chat này")
                .data(chatResponseDTOS)
                .build();
    }






}
