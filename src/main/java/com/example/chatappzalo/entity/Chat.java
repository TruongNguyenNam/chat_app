package com.example.chatappzalo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    public enum ChatType {
        PRIVATE, GROUP
    }

}
