package com.example.chatappzalo.repositories;

import com.example.chatappzalo.entity.MessageReactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageReactionsRepository extends JpaRepository<MessageReactions,Long> {

    List<MessageReactions> findByMessageId(Long messageId);

//    boolean existsBy(Long messageId);
@Query("""
    SELECT MIN(r.id),r.reaction, COUNT(r)
    FROM MessageReactions r
    WHERE r.message.id = :messageId
    GROUP BY r.reaction
""")
List<Object[]> countByReaction(Long messageId);

//    List<Object[]> countByReaction(Long messageId);

}
