package com.example.chatappzalo.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "Chat_Members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMember extends Auditable{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @ManyToOne
        @JoinColumn(name = "chat_id")
        private Chat chat;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @Column(nullable = false)
        private int unreadCount = 0;  // tăng số lượng lên để realtime

        private Long lastReadMessageId;  //

}


