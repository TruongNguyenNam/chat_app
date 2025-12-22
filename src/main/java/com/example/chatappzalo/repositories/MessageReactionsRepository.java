package com.example.chatappzalo.repositories;

import com.example.chatappzalo.entity.MessageReactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageReactionsRepository extends JpaRepository<MessageReactions,Long> {

    List<MessageReactions> findByMessageId(Long messageId);

}
