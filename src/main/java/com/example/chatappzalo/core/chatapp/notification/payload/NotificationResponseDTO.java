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
public class NotificationResponseDTO {

    private Long id;

    private String userFullName;


    private String NotificationType;

    private String content;

    private Long relatedId;

}
