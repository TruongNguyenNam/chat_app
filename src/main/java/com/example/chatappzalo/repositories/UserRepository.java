package com.example.chatappzalo.repositories;


import com.example.chatappzalo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<User> findByPhone(String phone);

    Boolean existsByEmail(String email);


    @Query("""
    SELECT u
    FROM User u
    WHERE u.phone = :phone
      AND u.id <> :currentUserId
      AND NOT EXISTS (
          SELECT 1
          FROM Contact c
          WHERE 
              (c.user.id = :currentUserId AND c.friend.id = u.id)
           OR (c.user.id = u.id AND c.friend.id = :currentUserId)
      )
""")
    Optional<User> findUserByPhoneNoContact(
            @Param("phone") String phone,
            @Param("currentUserId") Long currentUserId
    );




    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
    update User u
    set u.isOnline = :isOnline,
        u.lastActive = :lastActive
    where u.id = :userId
""")
    void updateOnline(
            @Param("userId") Long userId,
            @Param("isOnline") boolean isOnline,
            @Param("lastActive") LocalDateTime lastActive
    );



    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
    update User u
    set u.isOnline = :isOnline,
        u.lastActive = :lastActive
    where u.id = :userId
""")
    void updateOffLine(
            @Param("userId") Long userId,
            @Param("isOnline") boolean isOnline,
            @Param("lastActive") LocalDateTime lastActive
    );


    @Query("""
    SELECT u
    FROM User u
    WHERE u.isOnline = true
""")
    List<User> findAllOnlineUsers();





}


