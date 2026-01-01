package com.example.chatappzalo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_type")
    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    @Column(name = "chat_name", length = 100)
    private String chatName;   //dành cho nhóm chat

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private Set<ChatMember> members = new HashSet<>();


    @OneToMany(mappedBy = "chat",fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();
    public enum ChatType {
        PRIVATE, GROUP
    }

}
