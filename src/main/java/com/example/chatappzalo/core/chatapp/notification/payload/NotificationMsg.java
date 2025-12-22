package com.example.chatappzalo.core.chatapp.notification.payload;

import com.example.chatappzalo.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMsg {
    private Notification.NotificationType type;
    private String content;
    private Long relatedId;
    private Long senderId;
    private String senderUsername;
    private String senderDisplayName;
    private String senderAvatar;        // optional
    private LocalDateTime timestamp;

}
