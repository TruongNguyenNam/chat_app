package com.example.chatappzalo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// Entity for Messages
@Entity
@Table(name = "Messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private Long parent_message_id;


    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

//    @Column(name = "media_url", length = 255)
//    private String mediaUrl;

    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();

    @OneToMany(mappedBy = "message",fetch = FetchType.LAZY)
    private List<MessageReactions> messageReactions;


    @Column(name = "is_read")
    private boolean isRead = false;  // chắc chắn sẽ bỏ trường này vì cái này chỉ
                                     // 1 - 1, còn group thì sẽ không biết được đã đọc hay chưa

    public enum MessageType {
        TEXT, IMAGE, VIDEO, FILE, VOICE
    }
}

