package com.example.chatappzalo.core.chatapp.notification.payload;

import com.example.chatappzalo.entity.Notification;
import com.example.chatappzalo.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    private Long userId;

    private Notification.NotificationType type;

    private String content;

    private Long relatedId;


//    public enum NotificationType {
//        MESSAGE, CALL, FRIEND_REQUEST
//    }

}
