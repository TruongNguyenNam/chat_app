package com.example.chatappzalo.service.notification;

import com.example.chatappzalo.core.chatapp.notification.payload.NotificationRequestDTO;
import com.example.chatappzalo.core.chatapp.notification.payload.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    List<NotificationResponseDTO> getMyNotifications();

    void createNotification(NotificationRequestDTO notificationRequestDTO);






}
