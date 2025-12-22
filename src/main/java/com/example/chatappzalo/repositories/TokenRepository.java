package com.example.chatappzalo.repositories;
// Entity for Token

import com.example.chatappzalo.entity.Token;
import com.example.chatappzalo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface TokenRepository extends JpaRepository<Token,Long> {
    @Modifying
    void deleteByUser(User user);

    Token findByKeyAndType(String key, Token.TokenType type);
}
