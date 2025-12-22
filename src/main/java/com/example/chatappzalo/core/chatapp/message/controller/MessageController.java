package com.example.chatappzalo.core.chatapp.message.controller;

import com.example.chatappzalo.core.chatapp.message.payload.MessageRequestDTO;
import com.example.chatappzalo.core.chatapp.message.payload.MessageResponseDTO;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.message.MessageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/message")
@Validated
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final  MessageService messageService;

    @GetMapping("/chat/{chatId}")
    public ResponseData<List<MessageResponseDTO>> findByChatId(
            @PathVariable Long chatId
    ) {
        try {
            List<MessageResponseDTO> messages = messageService.findByChatId(chatId);
            return ResponseData.<List<MessageResponseDTO>>builder()
                    .status(200)
                    .message("Lấy tin nhắn của đoạn chat thành công ")
                    .data(messages)
                    .build();
        } catch (EntityNotFoundException e) {
            return ResponseData.<List<MessageResponseDTO>>builder()
                    .status(404)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseData.<List<MessageResponseDTO>>builder()
                    .status(500)
                    .message("Đã xảy ra lỗi: " + e.getMessage())
                    .build();
        }
    }


    @PostMapping(value = "/send", consumes = {"multipart/form-data"} )
    public ResponseData<Void> sendMessage(
            @RequestPart("message") MessageRequestDTO request,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        try {
            MultipartFile[] safeFiles = (files == null) ? new MultipartFile[0] : files;
            messageService.sendMessage(request, safeFiles);

            return ResponseData.<Void>builder()
                    .status(200)
                    .message("Gửi tin nhắn thành công")
                    .build();

        } catch (EntityNotFoundException e) {
            return ResponseData.<Void>builder().status(404).message(e.getMessage()).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseData.<Void>builder().status(400).message(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Lỗi khi gửi tin nhắn", e);
            return ResponseData.<Void>builder().status(500).message("Lỗi server").build();
        }
    }


    // Đánh dấu tin nhắn đã đọc
    @PutMapping("/{chatId}/read/{userId}")
    public ResponseData<Void> markMessagesAsRead(
            @PathVariable Long chatId,
            @PathVariable Long userId) {
        try {
            messageService.markMessagesAsRead(userId, chatId);
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





}
