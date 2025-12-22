package com.example.chatappzalo.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "Calls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Call extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "caller_id")
    private User caller;

    @Column(name = "call_type")
    @Enumerated(EnumType.STRING)
    private CallType callType;

    @Column(name = "start_time")
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CallStatus status;

    public enum CallType {
        VOICE, VIDEO
    }

    public enum CallStatus {
        ONGOING, COMPLETED, MISSED
    }



}
