package com.example.chatappzalo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
// Entity for Notifications
@Entity
@Table(name = "Notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "related_id")
    private Long relatedId;


    // cái này chưa đọc nếu đọc rồi là true
    @Column(name = "is_read")
    private boolean isRead = false;

    public enum NotificationType {
        MESSAGE, CALL, FRIEND_REQUEST
    }
}
