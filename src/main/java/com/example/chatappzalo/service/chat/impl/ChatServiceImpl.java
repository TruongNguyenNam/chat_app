package com.example.chatappzalo.service.chat.impl;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatReadStatusMsg;
import com.example.chatappzalo.core.chatapp.chat.payload.ChatRequestDTO;
import com.example.chatappzalo.core.chatapp.chat.payload.ChatResponseDTO;
import com.example.chatappzalo.core.chatapp.media.payload.MediaResponseDTO;
import com.example.chatappzalo.entity.*;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.ChatMemberRepository;
import com.example.chatappzalo.repositories.ChatRepository;
import com.example.chatappzalo.repositories.MessageRepository;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.chat.ChatService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final ChatMemberRepository chatMemberRepository;

    private final UserRepository userRepository;

    private final MessageRepository messageRepository;

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

//    @Override
//    @Transactional
//    public void markChatAsRead(Long chatId) {
//
//        Long userId = SecurityUtils.getCurrentUserId();
//
//        log.info("[CHAT-READ] User {} bắt đầu đánh dấu đã đọc chat {}", userId, chatId);
//
//        ChatMember member = chatMemberRepository
//                .findByChatIdAndUserId(chatId, userId)
//                .orElseThrow(() -> {
//                    log.warn("[CHAT-READ] User {} không phải member của chat {}", userId, chatId);
//                    return new EntityNotFoundException("Không phải thành viên chat");
//                });
//
//        Long lastMessageId = messageRepository
//                .findTopByChatIdOrderByIdDesc(chatId)
//                .map(Message::getId)
//                .orElse(null);
//
//        if (lastMessageId == null) {
//            log.info("[CHAT-READ] Chat {} chưa có message nào", chatId);
//            return;
//        }
//
//        Message lastMessage = messageRepository
//                .findTopByChatIdOrderByIdDesc(chatId)
//                .orElse(null);
//
//        if (lastMessage == null) return;
//
//        LocalDateTime readAt = lastMessage.getSentAt();
//
//        Long oldLastRead = member.getLastReadMessageId();
//        int oldUnread = member.getUnreadCount();
//
//        member.setUnreadCount(0);
//        member.setLastReadMessageId(lastMessageId);
////        member.set
//        chatMemberRepository.save(member);
//
//        log.info(
//                "[CHAT-READ] User {} đã đọc chat {} | lastRead: {} → {} | unread: {} → 0",
//                userId,
//                chatId,
//                oldLastRead,
//                lastMessageId,
//                oldUnread
//        );
//
//        // 🔔 notify realtime seen
//        messagingTemplate.convertAndSend(
//                "/topic/chat/" + chatId + "/read",
//                new ChatReadStatusMsg(chatId, userId, lastMessageId,readAt)
//        );
//
//        log.info(
//                "[CHAT-READ][WS] Đã gửi SEEN realtime | chatId={} userId={} lastReadMessageId={}",
//                chatId,
//                userId,
//                lastMessageId
//        );
//    }

    @Override
    @Transactional
    public void markChatAsRead(Long chatId) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("[CHAT-READ] User {} bắt đầu đánh dấu đã đọc chat {}", userId, chatId);

        ChatMember member = chatMemberRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> {
                    log.warn("[CHAT-READ] User {} không phải member của chat {}", userId, chatId);
                    return new EntityNotFoundException("Không phải thành viên chat");
                });

        // Chat chưa có message → không cần xử lý
        Message lastMessage = messageRepository
                .findTopByChatIdOrderByIdDesc(chatId)
                .orElse(null);

        if (lastMessage == null) {
            log.info("[CHAT-READ] Chat {} chưa có message nào", chatId);
            return;
        }

        Long lastMessageId = lastMessage.getId();
        LocalDateTime readAt = lastMessage.getSentAt();

        int oldUnread = member.getUnreadCount();
        Long oldLastRead = member.getLastReadMessageId();

        // Đã đọc rồi → bỏ qua
        if (oldUnread == 0 && Objects.equals(oldLastRead, lastMessageId)) {
            return;
        }

        // Reset unread
        member.setUnreadCount(0);
        member.setLastReadMessageId(lastMessageId);
        chatMemberRepository.save(member);

        log.info(
                "[CHAT-READ] User {} đọc chat {} | lastRead {} → {} | unread {} → 0",
                userId, chatId, oldLastRead, lastMessageId, oldUnread
        );

        // 🔔 Push SEEN cho chat đang mở
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/read",
                new ChatReadStatusMsg(chatId, userId, lastMessageId, readAt)
        );

        // 🔥 Push tổng unread mới (CHUẨN)
        long totalUnread = chatMemberRepository
                .sumUnreadMessagesByUserId(userId);

        messagingTemplate.convertAndSendToUser(
                member.getUser().getUsername(), // hoặc userId
                "/queue/unread-sync",
                totalUnread
        );

        log.info(
                "[CHAT-READ][WS] unread-sync total={} cho user {}",
                totalUnread, userId
        );

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
                dto.setFriendUserId(friend.getId());
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


        // ===== UNREAD COUNT (THEO USER HIỆN TẠI) =====
        ChatMember me = chat.getMembers().stream()
                .filter(cm -> cm.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        assert me != null;
        dto.setUnreadCount(me.getUnreadCount());
        Message lastMessage = messageRepository
                .findTopByChatIdOrderByIdDesc(chat.getId())
                .orElse(null);
        if(lastMessage != null){
            dto.setLastMessageContent(lastMessage.getContent());
            dto.setLastMessageTime(lastMessage.getSentAt());
        }

        return dto;
    }


}
