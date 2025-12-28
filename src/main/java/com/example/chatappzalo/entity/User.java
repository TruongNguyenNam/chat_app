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
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", length = 50,  unique = true)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_online")
    private Boolean isOnline = false;  // false: online, true : offline

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<ChatMember> members = new HashSet<>();

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private Set<MessageReactions> messageReactions;


    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @NoArgsConstructor
    @Getter
    public enum Role {
        ADMIN,   //quản lý task
        CUSTOMER  // danh sách người dùng
    }

    @NoArgsConstructor
    @Getter
    public enum Gender {
        Male,
        Female
    }

}


