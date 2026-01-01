package com.example.chatappzalo.core.chatapp.chat_member.controller;

import com.example.chatappzalo.core.chatapp.chat_member.payload.ChatMemberResponseDTO;
import com.example.chatappzalo.entity.ChatMember;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.chat_member.ChatMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat_member")
@Validated
@RequiredArgsConstructor
public class ChatMemberController {

    private final ChatMemberService chatMemberService;


    @GetMapping
    public ResponseData<Long> sumUnreadByMe(){
        Long sumUnread = chatMemberService.CountUnreadByMe();
        return ResponseData.<Long>builder()
                .data(sumUnread)
                .status(201)
                .message("lấy thành công danh sách tin nhắn chưa đọc của tôi thành công")
                .build();

    }

    @GetMapping("/{chatId}")
    public ResponseData<List<ChatMemberResponseDTO>> findByChatId(@PathVariable(name = "chatId") Long chatId){
        List<ChatMemberResponseDTO> chatMemberResponseDTOS = chatMemberService.findByChatId(chatId);
        return ResponseData.<List<ChatMemberResponseDTO>>builder()
                .status(201)
                .data(chatMemberResponseDTOS)
                .message("lấy danh sách thành viên trong nhóm chat thành công")
                .build();

    }






}
