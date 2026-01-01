package com.example.chatappzalo.core.chatapp.chat.controller;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatRequestDTO;
import com.example.chatappzalo.core.chatapp.chat.payload.ChatResponseDTO;
import com.example.chatappzalo.core.chatapp.contact.payload.ContactResponseDTO;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.chat.ChatService;
import com.example.chatappzalo.service.contact.ContactService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @PutMapping("/read/{chatId}")
    public ResponseData<Void> markMessagesAsRead(
            @PathVariable Long chatId
    ) {
        try {
            chatService.markChatAsRead(chatId);
            return ResponseData.<Void>builder()
                    .status(200)
                    .message("Đã đánh dấu tất cả tin nhắn là đã đọc")
                    .build();
        } catch (EntityNotFoundException e) {
            return ResponseData.<Void>builder()
                    .status(404)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseData.<Void>builder()
                    .status(500)
                    .message("Đã xảy ra lỗi: " + e.getMessage())
                    .build();
        }
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


    @PostMapping
    public ResponseData<Void> createGroupChat(
            @RequestBody ChatRequestDTO chatRequestDTO) {
        chatService.createChatGroup(chatRequestDTO);
        return ResponseData.<Void>builder()
                .status(201)
                .message("Tạo nhóm chat thành công")
                .data(null)
                .build();
    }









}
