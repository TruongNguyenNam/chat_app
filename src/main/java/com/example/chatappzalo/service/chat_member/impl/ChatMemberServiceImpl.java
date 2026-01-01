package com.example.chatappzalo.service.chat_member.impl;

import com.example.chatappzalo.core.chatapp.chat_member.payload.ChatMemberResponseDTO;
import com.example.chatappzalo.entity.Chat;
import com.example.chatappzalo.entity.ChatMember;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.ChatMemberRepository;
import com.example.chatappzalo.repositories.ChatRepository;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.chat_member.ChatMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMemberServiceImpl implements ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;

    private final ChatRepository chatRepository;

    private final UserRepository  userRepository;

    @Override
    @Transactional
    public Long CountUnreadByMe() {
        Long currentId = SecurityUtils.getCurrentUserId();
        return chatMemberRepository.sumUnreadMessagesByUserId(currentId);
    }

    @Override
    @Transactional
    public List<ChatMemberResponseDTO> findByChatId(Long chatId) {
        Long currentId = SecurityUtils.getCurrentUserId();
        List<ChatMember> chatMembers = chatMemberRepository.findByChatId(chatId);
        return chatMembers.stream()
                .filter(chatMember -> !chatMember.getUser().getId().equals(currentId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private ChatMemberResponseDTO mapToResponse(ChatMember chatMember){
        Chat chat = chatMember.getChat();

        String chatName = null;

        if (chat.getChatType() == Chat.ChatType.GROUP) {
            chatName = chat.getChatName();
        }

        return ChatMemberResponseDTO.builder()
                .id(chatMember.getId())
                .chatType(chat.getChatType().name())
                .chatName(chatName)
                .avatarUrl(chatMember.getUser().getAvatarUrl())
                .fullName(chatMember.getUser().getFullName())
                .phone(chatMember.getUser().getPhone())
                .build();

    }




}
