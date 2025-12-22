package com.example.chatappzalo.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "contact_type")
    @Enumerated(EnumType.STRING)
    private ContactType ContactType ;
    public enum ContactType {
       PENDING, ACCEPTED, REJECTED
    }

}
