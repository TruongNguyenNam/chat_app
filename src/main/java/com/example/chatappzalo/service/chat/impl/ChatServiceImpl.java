package com.example.chatappzalo.service.chat.impl;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatRequestDTO;
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
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final ChatMemberRepository chatMemberRepository;

    private final UserRepository userRepository;

    private final SimpMessageSendingOperations messagingTemplate;
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

    @Override
    @Transactional
    public void createChatGroup(ChatRequestDTO chatRequestDTO) {
        if (chatRepository.existsByChatName(chatRequestDTO.getChatName())) {
            throw new IllegalArgumentException("Tên nhóm chat đã tồn tại");
        }

        if (chatRequestDTO.getUserId() == null || chatRequestDTO.getUserId().size() < 2) {
            throw new IllegalArgumentException("Nhóm chat phải có ít nhất 2 người");
        }

        // 3. Lấy user
        List<User> users = userRepository.findAllById(chatRequestDTO.getUserId());

        if (users.size() != chatRequestDTO.getUserId().size()) {
            throw new IllegalArgumentException("Một hoặc nhiều user không tồn tại");
        }
        Chat chat = new Chat();

        chat.setChatName(chatRequestDTO.getChatName());
        chat.setChatType(Chat.ChatType.GROUP);
        Chat chat1 = chatRepository.save(chat);

        List<ChatMember> chatMembers = new ArrayList<>();
        for(User user : users){
            ChatMember chatMember = new ChatMember();
            chatMember.setUser(user);
            chatMember.setChat(chat1);
            chatMembers.add(chatMember);
        }
        chatMemberRepository.saveAll(chatMembers);

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
                dto.setIsOnline(friend.getIsOnline());
                dto.setLastActive(friend.getLastActive());
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
