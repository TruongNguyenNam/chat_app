package com.example.chatappzalo.service.message.impl;

import com.example.chatappzalo.core.chatapp.chat.payload.ChatListUpdateMsg;
import com.example.chatappzalo.core.chatapp.chat.presence.ActiveChatTracker;
import com.example.chatappzalo.core.chatapp.message.payload.ChatMessage;
import com.example.chatappzalo.core.chatapp.message.payload.MessageRequestDTO;
import com.example.chatappzalo.core.chatapp.message.payload.MessageResponseDTO;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationMsg;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationRequestDTO;
import com.example.chatappzalo.entity.*;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.*;
import com.example.chatappzalo.service.cloudinary.CloudinaryService;
import com.example.chatappzalo.service.message.MessageService;
import com.example.chatappzalo.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final MediaRepository mediaRepository;

    private final MessageRepository messageRepository;

    private final ChatMemberRepository chatMemberRepository;

    private final MessageReactionsRepository messageReactionsRepository;

    private final SimpMessageSendingOperations messagingTemplate;

    private final ActiveChatTracker activeChatTracker;

    private final CloudinaryService cloudinaryService;

    private final NotificationService notificationService;


//    @Override
//    @Transactional
//    public void sendMessage(MessageRequestDTO request, MultipartFile[] files) throws IOException {
//        Long senderId = SecurityUtils.getCurrentUserId();
//        log.info("userID là bản thân " + senderId);
//        // 1. Validate sender
//        User sender = userRepository.findById(senderId)
//                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));
//
//        // 2. Validate chat
//        Chat chat = chatRepository.findById(request.getChatId())
//                .orElseThrow(() -> new EntityNotFoundException("Cuộc trò chuyện không tồn tại"));
//
//        // 3. Check membership
//        if (!chatMemberRepository.existsByChatIdAndUserId(chat.getId(), sender.getId())) {
//            throw new IllegalStateException("Bạn không phải thành viên của cuộc trò chuyện này");
//        }
//
//        // 4. Validate message type
//        Message.MessageType messageType;
//        try {
//            messageType = Message.MessageType.valueOf(request.getMessageType());
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Loại tin nhắn không hợp lệ: " + request.getMessageType());
//        }
//
//        // 5. Nếu có file thì phải là loại media
//        boolean hasFiles = files != null && files.length > 0 && Arrays.stream(files).anyMatch(f -> !f.isEmpty());
//        if (hasFiles && !isMediaMessageType(messageType)) {
//            throw new IllegalArgumentException("Chỉ được đính kèm file cho loại IMAGE, VIDEO, VOICE, FILE,STICKER");
//        }
//
//        // 6. Nếu là TEXT hoặc STICKER thì không được có file
//        if (!isMediaMessageType(messageType) && hasFiles) {
//            throw new IllegalArgumentException("Loại tin nhắn này không hỗ trợ đính kèm file");
//        }
//
//        Long parentMessageId = null;
//        if (request.getParentMessageId() != null) {
//
//            Message parentMessage = messageRepository.findById(request.getParentMessageId())
//                    .orElseThrow(() ->
//                            new EntityNotFoundException("Tin nhắn được phản hồi không tồn tại")
//                    );
//
//            // đảm bảo reply trong cùng chat
//            if (!parentMessage.getChat().getId().equals(chat.getId())) {
//                throw new IllegalArgumentException(
//                        "Không thể phản hồi tin nhắn thuộc cuộc trò chuyện khác"
//                );
//            }
//
//            if (Boolean.TRUE.equals(parentMessage.getDeleted())) {
//                throw new IllegalStateException("Không thể phản hồi tin nhắn đã bị xóa");
//            }
//
//            parentMessageId = parentMessage.getId(); // 👈 CHỈ LẤY ID
//        }
//
//
//        // 7. Tạo message
//        Message message = new Message();
//        message.setSender(sender);
//        message.setChat(chat);
//        message.setContent(request.getContent());
//        message.setMessageType(messageType);
//        message.setParent_message_id(parentMessageId);
//        message.setRead(false);
//        message.setDeleted(false);
//        message.setSentAt(LocalDateTime.now());
//
//        message = messageRepository.save(message);
//
//        // 8. Upload và lưu media nếu có file
//        if (hasFiles) {
//            for (MultipartFile file : files) {
//                if (file.isEmpty()) continue;
//
//                Map<String, Object> uploadResult = cloudinaryService.uploadFile(file, "chat_media");
//                String mediaUrl = cloudinaryService.getMediaUrl(uploadResult);
//
//                Media media = new Media();
//                media.setMessage(message);
//                media.setUser(sender);
//                media.setMediaUrl(mediaUrl);
//                media.setMediaType(mapToMediaType(messageType));
//                // audit fields nếu cần
//                mediaRepository.save(media);
//            }
//        }
//
//        // 9. Lấy danh sách media URL để broadcast
//        List<String> mediaUrls = mediaRepository.findByMessageId(message.getId())
//                .stream()
//                .map(Media::getMediaUrl)
//                .toList();
//
//        // 10. Broadcast qua WebSocket
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setMessageId(message.getId());
////        chatMessage.setSenderId(sender.getId());
//        chatMessage.setSenderId(senderId);
//        chatMessage.setSenderName(sender.getFullName());
//        chatMessage.setSenderAvatar(sender.getAvatarUrl());
//        chatMessage.setChatId(chat.getId());
//        chatMessage.setContent(message.getContent());
//        chatMessage.setMessageType(messageType.name());
//        chatMessage.setSentAt(message.getSentAt());
//        chatMessage.setMediaUrls(mediaUrls);
//        chatMessage.setParentMessageId(parentMessageId);
//
//        messagingTemplate.convertAndSend("/topic/chat/" + chat.getId(), chatMessage);
//
//        // 1️⃣ Lấy danh sách tất cả thành viên chat (userId + username)
//        List<ChatMember> members = chatMemberRepository.findByChatId(chat.getId());
//
//        Map<Long, String> userIdToUsername = members.stream()
//                .collect(Collectors.toMap(
//                        cm -> cm.getUser().getId(),
//                        cm -> cm.getUser().getUsername()
//                ));
//
//// 2️⃣ Danh sách userId
//        List<Long> memberUserIds = new ArrayList<>(userIdToUsername.keySet());
//
//// 3️⃣ Những người đang mở chat này
//        Set<Long> activeUsersInThisChat =
//                activeChatTracker.getActiveUsersInChat(chat.getId());
//
//// 4️⃣ Lọc userId cần nhận notification
//        List<Long> receiverUserIds = memberUserIds.stream()
//                .filter(userId -> !userId.equals(senderId))                 // không gửi cho người gửi
//                .filter(userId -> !activeUsersInThisChat.contains(userId)) // không gửi người đang mở chat
//                .toList();
//
//        if (!receiverUserIds.isEmpty()) {
//
//            log.info("Có {} người cần nhận notification", receiverUserIds.size());
//
//            // 5️⃣ Convert sang username để gửi WebSocket
//            List<String> receiverUsernames = receiverUserIds.stream()
//                    .map(userIdToUsername::get)
//                    .filter(Objects::nonNull)
//                    .toList();
//
//            // 6️⃣ Tạo notification real-time
//            NotificationMsg realTimeMsg = new NotificationMsg();
//            realTimeMsg.setType(Notification.NotificationType.MESSAGE);
//            realTimeMsg.setContent("Bạn có tin nhắn mới từ " + sender.getFullName());
//            realTimeMsg.setRelatedId(chat.getId());
//            realTimeMsg.setSenderId(senderId);
//            realTimeMsg.setSenderUsername(sender.getUsername());
//            realTimeMsg.setSenderDisplayName(sender.getFullName());
//            realTimeMsg.setSenderAvatar(sender.getAvatarUrl());
//            realTimeMsg.setTimestamp(LocalDateTime.now());
//
//            // 7️⃣ GỬI REALTIME (username)
//            notificationService.sendToUsersV2(receiverUsernames, realTimeMsg);
//
//            // 8️⃣ LƯU DB (userId)
//            for (Long receiverId : receiverUserIds) {
//                NotificationRequestDTO requestDTO = new NotificationRequestDTO();
//                requestDTO.setUserId(receiverId);
//                requestDTO.setType(Notification.NotificationType.MESSAGE);
//                requestDTO.setContent(sender.getFullName() + " đã gửi một tin nhắn");
//                requestDTO.setRelatedId(chat.getId());
//
//                notificationService.createNotification(requestDTO);
//            }
//        }
//
//// 9️⃣ Cập nhật chat
//        chat.setLastModifiedDate(LocalDateTime.now());
//        chatRepository.save(chat);
//
//    }


    @Override
    @Transactional
    public void sendMessage(MessageRequestDTO request, MultipartFile[] files) throws IOException {

        /* ======================================================
         * 0. CONTEXT
         * ====================================================== */
        Long senderId = SecurityUtils.getCurrentUserId();
//        String senderUsername = SecurityUtils.getCurrentUsername();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));

        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Cuộc trò chuyện không tồn tại"));

        if (!chatMemberRepository.existsByChatIdAndUserId(chat.getId(), senderId)) {
            throw new IllegalStateException("Bạn không phải thành viên của cuộc trò chuyện này");
        }

        /* ======================================================
         * 1. MESSAGE TYPE
         * ====================================================== */
        Message.MessageType messageType;
        try {
            messageType = Message.MessageType.valueOf(request.getMessageType());
        } catch (Exception e) {
            throw new IllegalArgumentException("Loại tin nhắn không hợp lệ");
        }

        boolean hasFiles = files != null && Arrays.stream(files).anyMatch(f -> !f.isEmpty());

        if (hasFiles && !isMediaMessageType(messageType)) {
            throw new IllegalArgumentException("Loại tin nhắn này không hỗ trợ file");
        }

        /* ======================================================
         * 2. REPLY MESSAGE (OPTIONAL)
         * ====================================================== */
        Long parentMessageId = null;

        if (request.getParentMessageId() != null) {
            Message parent = messageRepository.findById(request.getParentMessageId())
                    .orElseThrow(() -> new EntityNotFoundException("Tin nhắn phản hồi không tồn tại"));

            if (!parent.getChat().getId().equals(chat.getId())) {
                throw new IllegalArgumentException("Reply sai chat");
            }
            if (Boolean.TRUE.equals(parent.getDeleted())) {
                throw new IllegalStateException("Không thể reply tin nhắn đã xóa");
            }

            parentMessageId = parent.getId();
        }

        /* ======================================================
         * 3. SAVE MESSAGE
         * ====================================================== */
        Message message = new Message();
        message.setSender(sender);
        message.setChat(chat);
        message.setContent(request.getContent());
        message.setMessageType(messageType);
        message.setParent_message_id(parentMessageId);
        message.setRead(false);
        message.setDeleted(false);
        message.setSentAt(LocalDateTime.now());

        message = messageRepository.save(message);

        /* ======================================================
         * 4. MEDIA (OPTIONAL)
         * ====================================================== */
        if (hasFiles) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                Map<String, Object> upload = cloudinaryService.uploadFile(file, "chat_media");

                Media media = new Media();
                media.setMessage(message);
                media.setUser(sender);
                media.setMediaUrl(cloudinaryService.getMediaUrl(upload));
                media.setMediaType(mapToMediaType(messageType));

                mediaRepository.save(media);
            }
        }

        List<String> mediaUrls = mediaRepository.findByMessageId(message.getId())
                .stream()
                .map(Media::getMediaUrl)
                .toList();

        /* ======================================================
         * 5. BROADCAST CHAT MESSAGE
         * ====================================================== */
        ChatMessage wsMessage = new ChatMessage();
        wsMessage.setMessageId(message.getId());
        wsMessage.setChatId(chat.getId());
        wsMessage.setSenderId(senderId);
        wsMessage.setSenderName(sender.getFullName());
        wsMessage.setSenderAvatar(sender.getAvatarUrl());
        wsMessage.setContent(message.getContent());
        wsMessage.setMessageType(messageType.name());
        wsMessage.setMediaUrls(mediaUrls);
        wsMessage.setSentAt(message.getSentAt());
        wsMessage.setParentMessageId(parentMessageId);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chat.getId(),
                wsMessage
        );

        /* ======================================================
         * 6. UNREAD + BADGE REALTIME
         * ====================================================== */
        List<ChatMember> members = chatMemberRepository.findByChatId(chat.getId());
        Set<Long> activeUsers = activeChatTracker.getActiveUsersInChat(chat.getId());

        for (ChatMember member : members) {

            Long memberId = member.getUser().getId();

            if (memberId.equals(senderId)) continue;

            if (!activeUsers.contains(memberId)) {

                // +1 unread
                member.setUnreadCount(member.getUnreadCount() + 1);
                chatMemberRepository.save(member);

                ChatListUpdateMsg dto = ChatListUpdateMsg.builder()
                        .chatId(chat.getId())
                        .lastMessageId(message.getId())
                        .lastMessageContent(message.getContent())
                        .lastMessageTime(message.getSentAt())
                        .senderId(senderId)
                        .senderName(sender.getFullName())
                        .unreadCount(member.getUnreadCount())
                        .build();

                messagingTemplate.convertAndSendToUser(
                        member.getUser().getUsername(),
                        "/queue/chat-list-update",
                        dto
                );
            }

        }

        /* ======================================================
         * 7. NOTIFICATION (KHÔNG DÙNG LÀM BADGE)
         * ====================================================== */
        List<ChatMember> offlineMembers = members.stream()
                .filter(cm -> !cm.getUser().getId().equals(senderId))
                .filter(cm -> !activeUsers.contains(cm.getUser().getId()))
                .toList();

        if (!offlineMembers.isEmpty()) {

            NotificationMsg noti = new NotificationMsg();
            noti.setType(Notification.NotificationType.MESSAGE);
            noti.setContent(sender.getFullName() + " đã gửi một tin nhắn");
            noti.setRelatedId(chat.getId());
            noti.setSenderId(senderId);
            noti.setSenderUsername(sender.getFullName());
            noti.setSenderDisplayName(sender.getFullName());
            noti.setSenderAvatar(sender.getAvatarUrl());
            noti.setTimestamp(LocalDateTime.now());

            List<String> usernames = offlineMembers.stream()
                    .map(cm -> cm.getUser().getUsername())
                    .toList();

            notificationService.sendToUsersV2(usernames, noti);

            for (ChatMember cm : offlineMembers) {
                NotificationRequestDTO dto = new NotificationRequestDTO();
                dto.setUserId(cm.getUser().getId());
                dto.setType(Notification.NotificationType.MESSAGE);
                dto.setContent(noti.getContent());
                dto.setRelatedId(chat.getId());
                notificationService.createNotification(dto);
            }
        }

        /* ======================================================
         * 8. UPDATE CHAT TIME
         * ====================================================== */
        chat.setLastModifiedDate(LocalDateTime.now());
        chatRepository.save(chat);
    }


    private boolean isMediaMessageType(Message.MessageType type) {
        return type == Message.MessageType.IMAGE ||
                type == Message.MessageType.VIDEO ||
                type == Message.MessageType.VOICE ||
//                type == Message.MessageType.STICKER ||
                type == Message.MessageType.FILE;
    }

    private Media.MediaType mapToMediaType(Message.MessageType messageType) {
        return switch (messageType) {
            case IMAGE -> Media.MediaType.IMAGE;
            case VIDEO -> Media.MediaType.VIDEO;
            case VOICE -> Media.MediaType.VOICE;
//            case STICKER -> Media.MediaType.STICKER;
            case FILE -> Media.MediaType.FILE;
            default -> throw new IllegalArgumentException("Không hỗ trợ media type này");
        };
    }

    @Override
    @Transactional
    public List<MessageResponseDTO> findByChatId(Long chatId) {
        List<Message> messages = messageRepository.findByChatId(chatId);
        return messages.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markChatAsRead(Long chatId) {

        Long userId = SecurityUtils.getCurrentUserId();

        ChatMember member = chatMemberRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Không phải thành viên chat"));

        // Lấy message mới nhất
        Long lastMessageId = messageRepository
                .findTopByChatIdOrderByIdDesc(chatId)
                .map(Message::getId)
                .orElse(null);

        if (lastMessageId == null) return;

        member.setUnreadCount(0);
        member.setLastReadMessageId(lastMessageId);
        chatMemberRepository.save(member);

        // 🔔 notify seen realtime
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId + "/read",
                Map.of(
                        "chatId", chatId,
                        "userId", userId,
                        "lastReadMessageId", lastMessageId
                )
        );
    }

    @Override
    @Transactional
    public Long deleteMessage(Long messageId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin nhắn"));

        if (!message.getSender().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền xoá tin nhắn này");
        }

        message.setDeleted(true);
        messageRepository.save(message);

        return message.getChat().getId(); // 👈 CHỈ LẤY chatId
    }



    private MessageResponseDTO mapToResponse(Message message){
        MessageResponseDTO dto = new MessageResponseDTO();

        dto.setId(message.getId());
        dto.setChatId(message.getChat().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setSenderAvatar(message.getSender().getAvatarUrl());
        dto.setMessageType(message.getMessageType().name());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());

        dto.setDeleted(message.getDeleted());

        if (message.getDeleted()) {
            dto.setContent(null);
            dto.setMediaUrl(List.of());
            dto.setReactions(List.of());
        } else {
            dto.setContent(message.getContent());

            List<String> mediaUrls = mediaRepository.findByMessageId(message.getId())
                    .stream()
                    .map(Media::getMediaUrl)
                    .toList();
            dto.setMediaUrl(mediaUrls);

            List<String> reactions = messageReactionsRepository.findByMessageId(message.getId())
                    .stream()
                    .map(MessageReactions::getReaction)
                    .toList();
            dto.setReactions(reactions);
        }

        return dto;
    }



}
