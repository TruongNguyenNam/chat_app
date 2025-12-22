package com.example.chatappzalo.core.chatapp.media.payload;

import com.example.chatappzalo.entity.Media;
import com.example.chatappzalo.entity.Message;
import com.example.chatappzalo.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResponseDTO {

    private Long id;

    private LocalDateTime messageSentAt;

    private String fullName;

    private String avatarUrl;

    private String mediaUrl;

    private String mediaType;



}
