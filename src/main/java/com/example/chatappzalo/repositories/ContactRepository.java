package com.example.chatappzalo.repositories;


import com.example.chatappzalo.entity.Contact;
import com.example.chatappzalo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact,Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);


    @Query("""
        select c from Contact c
        where c.friend.id = :userId
        and c.ContactType = 'PENDING'
    """)
    List<Contact> findPendingRequests(Long userId);



    @Query("SELECT c FROM Contact c WHERE c.ContactType = 'ACCEPTED' AND (c.user.id = :userId OR c.friend.id = :userId)")
    List<Contact> findAcceptedContactsByUserId(@Param("userId") Long userId);






}
