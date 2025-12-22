package com.example.chatappzalo.service.chat.impl;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatResponseDTO;
import com.example.chatappzalo.core.chatapp.media.payload.MediaResponseDTO;
import com.example.chatappzalo.entity.*;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.ChatMemberRepository;
import com.example.chatappzalo.repositories.ChatRepository;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.chat.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final ChatMemberRepository chatMemberRepository;

    private final UserRepository userRepository;


    @Override
    @Transactional
    public List<ChatResponseDTO> getAllByChatType() {
        Long currentId = SecurityUtils.getCurrentUserId();
        List<Chat> chats = chatRepository.findAllChatsOfUser(currentId);
        return chats.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatResponseDTO findByChatId(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new IllegalArgumentException("không tìm thấy đoạn chat mày" + chatId)
        );

        return mapToResponse(chat);
    }


    private ChatResponseDTO mapToResponse(Chat chat) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        ChatResponseDTO dto = new ChatResponseDTO();
        dto.setChatId(chat.getId());
        dto.setChatType(chat.getChatType().name());

        if (chat.getChatType() == Chat.ChatType.PRIVATE) {

            // 🔥 Lấy user còn lại trong chat 1–1
            User friend = chat.getMembers().stream()
                    .map(ChatMember::getUser)
                    .filter(user -> !user.getId().equals(currentUserId))
                    .findFirst()
                    .orElse(null);

            if (friend != null) {
                dto.setFullName(friend.getFullName());
                dto.setAvatarUrl(friend.getAvatarUrl());
                dto.setGender(friend.getGender() != null ? friend.getGender().name() : null);
            }

        } else { // GROUP
            dto.setChatName(chat.getChatName());
            dto.setFullName(chat.getChatName());
            dto.setAvatarUrl(null); // sau này có thể thêm avatar nhóm
            dto.setGender(null);
        }

        return dto;
    }


}
