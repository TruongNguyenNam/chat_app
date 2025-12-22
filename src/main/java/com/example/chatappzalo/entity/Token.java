package com.example.chatappzalo.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

// Entity for Token
@Entity
@Table(name = "Token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "key", length = 255, unique = true)
    private String key;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TokenStatus status = TokenStatus.ACTIVE;

    @Column(name = "expired_date")
    private Date expiredDate;


    public enum TokenType {
        REFRESH_TOKEN, REGISTER, FORGOT_PASSWORD, OTP, SSO_TOKEN
    }

    public enum TokenStatus {
        ACTIVE, REVOKED, EXPIRED
    }

    public Token(User user, String key, TokenType type, Date expiredDate) {
        this.user = user;
        this.key = key;
        this.type = type;
        this.expiredDate = expiredDate;
    }


}
