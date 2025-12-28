package com.example.chatappzalo.service.notification.impl;

import com.example.chatappzalo.core.chatapp.notification.payload.NotificationMsg;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationRequestDTO;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationResponseDTO;
import com.example.chatappzalo.entity.Notification;
import com.example.chatappzalo.entity.User;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.NotificationRepository;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

//    public void sendToUsers(List<Long> userIds, NotificationMsg notificationMsg) {
//        log.info("GỬI NOTIFICATION REAL-TIME cho {} người: {}", userIds.size(), notificationMsg);
//        for (Long userId : userIds) {
//            messagingTemplate.convertAndSendToUser(
//                    userId.toString(),
//                    "/notifications",  // frontend sẽ subscribe /user/notifications
//                    notificationMsg
//            );
//        }
//    }

    @Override
    public void sendToUsersV2(List<String> usernames, NotificationMsg msg) {
        if (usernames == null || usernames.isEmpty()) return;

        usernames.forEach(username -> {
            log.debug("Send WS notification to {}", username);
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications",
                    msg
            );
        });
    }

    @Override
    @Transactional
    public void createNotification(NotificationRequestDTO notificationRequestDTO) {
        User user = userRepository.findById(notificationRequestDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(notificationRequestDTO.getType());
        notification.setContent(notificationRequestDTO.getContent());
        notification.setRelatedId(notificationRequestDTO.getRelatedId());
        notification.setRead(false);

        notificationRepository.save(notification);


    }

    @Override
    @Transactional
    public List<NotificationResponseDTO> getMyNotifications() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Notification> notification = notificationRepository.findByUserIdOrderByCreatedDateDesc(userId);
        return notification.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }




    @Override
    @Transactional
    public Long countNotification() {
        Long currentId = SecurityUtils.getCurrentUserId();
        return notificationRepository.countUnreadByUserId(currentId);
    }


    private NotificationResponseDTO fromEntity(Notification n) {
        NotificationResponseDTO responseDTO = new NotificationResponseDTO();
        responseDTO.setId(n.getId());
        responseDTO.setContent(n.getContent());
        responseDTO.setRelatedId(n.getRelatedId());
        responseDTO.setUserFullName(n.getUser().getFullName());
        responseDTO.setNotificationType(n.getType().name());
        return responseDTO;
    }



}
