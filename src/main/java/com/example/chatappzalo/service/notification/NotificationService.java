package com.example.chatappzalo.service.notification;

import com.example.chatappzalo.core.chatapp.notification.payload.NotificationMsg;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationRequestDTO;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    List<NotificationResponseDTO> getMyNotifications();

    void createNotification(NotificationRequestDTO notificationRequestDTO);


    Long countNotification();

//     void sendToUsers(List<Long> userIds, NotificationMsg notificationMsg);

     void sendToUsersV2(List<String> usernames, NotificationMsg notificationMsg);




}
