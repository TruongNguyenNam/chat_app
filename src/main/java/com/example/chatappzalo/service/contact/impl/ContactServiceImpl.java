package com.example.chatappzalo.service.contact.impl;

import com.example.chatappzalo.core.chatapp.contact.payload.ContactRequestDTO;
import com.example.chatappzalo.core.chatapp.contact.payload.ContactResponseDTO;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationMsg;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationRequestDTO;
import com.example.chatappzalo.entity.*;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.ChatMemberRepository;
import com.example.chatappzalo.repositories.ChatRepository;
import com.example.chatappzalo.repositories.ContactRepository;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.contact.ContactService;
import com.example.chatappzalo.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final UserRepository userRepository;

    private final ContactRepository contactRepository;

    private final ModelMapper modelMapper;

    private final ChatRepository chatRepository;

    private final ChatMemberRepository chatMemberRepository;

    private final NotificationService notificationService;

    private final SimpMessageSendingOperations messagingTemplate;


    @Transactional
    @Override
    public List<ContactResponseDTO> findByFriends() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Contact> contacts = contactRepository.findAcceptedContactsByUserId(userId);

        return contacts.stream()
                .map(contact -> {
                    // Xác định người bạn (người còn lại)
                    User friend = contact.getUser().getId().equals(userId)
                            ? contact.getFriend()
                            : contact.getUser();

                    // Tìm chat riêng giữa 2 người
                    Optional<Long> chatIdOpt = chatRepository
                            .findPrivateChatIdBetween(userId, friend.getId());

                    return new ContactResponseDTO(
                            contact.getId(),
                            friend.getFullName(),// id của contact (có thể dùng hoặc không)
                            friend.getGender() != null ? friend.getGender().name() : null,

                            friend.getAvatarUrl(),
                            friend.getEmail(),
                            friend.getPhone(),
                            friend.getId(),  // friendId
                            chatIdOpt.orElse(null),             // chatId (null nếu chưa từng nhắn)
                            contact.getContactType().name()     // ACCEPTED / PENDING / REJECTED
                    );
                })
                .toList();
    }






    @Override
    @Transactional
    public void addFriend(ContactRequestDTO contactRequestDTO) {
        Long senderId = SecurityUtils.getCurrentUserId();
        Long friendId = contactRequestDTO.getFriendId();

        // ❌ Không được kết bạn với chính mình
        if (senderId.equals(friendId)) {
            throw new IllegalArgumentException("Không thể kết bạn với chính mình");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend không tồn tại"));

        // ❌ Kiểm tra quan hệ đã tồn tại (2 chiều)
        boolean exists =
                contactRepository.existsByUserIdAndFriendId(senderId, friendId)
                        || contactRepository.existsByUserIdAndFriendId(friendId, senderId);

        if (exists) {
            throw new IllegalStateException("Quan hệ kết bạn đã tồn tại");
        }

        Contact contact = new Contact();
        contact.setUser(sender);
        contact.setFriend(friend);
        contact.setContactType(Contact.ContactType.PENDING);


        contactRepository.save(contact);

        NotificationRequestDTO notificationDTO = new NotificationRequestDTO();
        notificationDTO.setUserId(friendId);
        notificationDTO.setType(Notification.NotificationType.FRIEND_REQUEST);
        notificationDTO.setContent(sender.getUsername() + " đã gửi lời mời kết bạn");
        notificationDTO.setRelatedId(contact.getId());

        notificationService.createNotification(notificationDTO);

        String displayName = sender.getFullName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = sender.getUsername();
        } else {
            displayName = displayName.trim();
        }

        // Tạo message đơn giản để gửi cho frontend
        NotificationMsg realtimeMessage = new NotificationMsg();
        realtimeMessage.setType(Notification.NotificationType.FRIEND_REQUEST);
        realtimeMessage.setContent(displayName + " đã gửi lời mời kết bạn");
        realtimeMessage.setRelatedId(contact.getId());
        realtimeMessage.setSenderId(senderId);
        realtimeMessage.setSenderUsername(sender.getUsername());
        realtimeMessage.setSenderDisplayName(displayName);
        realtimeMessage.setSenderAvatar(sender.getAvatarUrl()); // nếu có field avatar
        realtimeMessage.setTimestamp(LocalDateTime.now());

        // Push riêng tư đến người nhận (friend)
        messagingTemplate.convertAndSendToUser(
                friend.getUsername(),           // username chính xác như trong Principal
                "/queue/notifications",         // destination mà FE subscribe
                realtimeMessage
        );

        // Optional: log để debug
        log.info("Đã push real-time friend request từ {} đến {}", sender.getUsername(), friend.getUsername());

    }

    @Override
    @Transactional
    public List<ContactResponseDTO> getPendingRequests() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("cái này conatct ID: " + currentUserId);
        if (currentUserId == null) {
            throw new AccessDeniedException("Chưa đăng nhập");
        }

        // 1. Lấy danh sách Contact pending
        List<Contact> contacts = contactRepository.findPendingRequests(currentUserId);

        // 2. Map sang DTO
      return   contacts.stream()
                .map(contact -> {
                    // Xác định người bạn (người còn lại)
                    User friend = contact.getUser().getId().equals(currentUserId)
                            ? contact.getFriend()
                            : contact.getUser();

                    // Tìm chat riêng giữa 2 người
                    Optional<Long> chatIdOpt = chatRepository
                            .findPrivateChatIdBetween(currentUserId, friend.getId());

                    return new ContactResponseDTO(
                            contact.getId(),
                            friend.getFullName(),// id của contact (có thể dùng hoặc không)
                            friend.getGender() != null ? friend.getGender().name() : null,

                            friend.getAvatarUrl(),
                            friend.getEmail(),
                            friend.getPhone(),
                            friend.getId(),  // friendId
                            chatIdOpt.orElse(null),             // chatId (null nếu chưa từng nhắn)
                            contact.getContactType().name()     // ACCEPTED / PENDING / REJECTED
                    );
                })
                .toList();

    }

    @Override
    @Transactional
    public void rejectFriendRequest(Long contactId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Chưa đăng nhập");
        }

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lời mời"));

        if (!contact.getFriend().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Không có quyền xử lý lời mời này");
        }

        if (contact.getContactType() != Contact.ContactType.PENDING) {
            throw new IllegalStateException("Lời mời không còn ở trạng thái chờ");
        }

        contact.setContactType(Contact.ContactType.REJECTED);
        contactRepository.save(contact);
    }

    @Override
    @Transactional
    public void acceptedFriendRequest(Long contactId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Chưa đăng nhập");
        }

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lời mời"));

        if (!contact.getFriend().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Không có quyền xử lý lời mời này");
        }

        if (contact.getContactType() != Contact.ContactType.PENDING) {
            throw new IllegalStateException("Lời mời không còn ở trạng thái chờ");
        }

        contact.setContactType(Contact.ContactType.ACCEPTED);
        contactRepository.save(contact);

        createPrivateChat(contact.getUser(), contact.getFriend());
    }

    private void createPrivateChat(User u1, User u2) {
        Chat chat = new Chat();
        chat.setChatType(Chat.ChatType.PRIVATE);
        chat.setChatName(null);
        chatRepository.save(chat);

        ChatMember m1 = new ChatMember();
        m1.setChat(chat);
        m1.setUser(u1);

        ChatMember m2 = new ChatMember();
        m2.setChat(chat);
        m2.setUser(u2);

        chatMemberRepository.saveAll(List.of(m1, m2));
    }

    @Override
    @Transactional
    public Long acceptedFriendRequestV2(Long contactId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("Chưa đăng nhập");
        }

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lời mời"));

        if (!contact.getFriend().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Không có quyền xử lý lời mời này");
        }

        if (contact.getContactType() != Contact.ContactType.PENDING) {
            throw new IllegalStateException("Lời mời không còn ở trạng thái chờ");
        }

        // 1️⃣ cập nhật trạng thái
        contact.setContactType(Contact.ContactType.ACCEPTED);
        contactRepository.save(contact);

        // 2️⃣ tìm hoặc tạo chat
        Chat chat = chatRepository
                .findPrivateChat(
                        contact.getUser().getId(),
                        contact.getFriend().getId()
                )
                .orElseGet(() ->
                        createPrivateChatV2(
                                contact.getUser(),
                                contact.getFriend()
                        )
                );

        return chat.getId();
    }

    private Chat createPrivateChatV2(User u1, User u2) {

        Chat chat = new Chat();
        chat.setChatType(Chat.ChatType.PRIVATE);
        chatRepository.save(chat);

        ChatMember m1 = new ChatMember();
        m1.setChat(chat);
        m1.setUser(u1);

        ChatMember m2 = new ChatMember();
        m2.setChat(chat);
        m2.setUser(u2);

        chatMemberRepository.saveAll(List.of(m1, m2));

        return chat;
    }








}
