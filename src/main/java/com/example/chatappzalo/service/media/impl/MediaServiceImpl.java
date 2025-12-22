package com.example.chatappzalo.service.media.impl;

import com.example.chatappzalo.core.chatapp.media.payload.MediaResponseDTO;
import com.example.chatappzalo.entity.Media;
import com.example.chatappzalo.entity.Message;
import com.example.chatappzalo.entity.User;
import com.example.chatappzalo.repositories.ChatRepository;
import com.example.chatappzalo.repositories.MediaRepository;
import com.example.chatappzalo.repositories.MessageRepository;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.media.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    private final MessageRepository messageRepository;

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    @Override
    public List<MediaResponseDTO> findByChatID(Long chatId, List<Media.MediaType> mediaType) {
        List<Media.MediaType> mediaTypes = List.of(Media.MediaType.IMAGE,
                Media.MediaType.VIDEO,
                Media.MediaType.FILE,
                Media.MediaType.VOICE);

        List<Media> media = mediaRepository.findByMessageChatId(chatId,mediaTypes);

        return media.stream().map(this::maptoResponse).collect(Collectors.toList());
    }

    private MediaResponseDTO maptoResponse(Media media){
        Message message = media.getMessage();
        User sender = message.getSender();

        return MediaResponseDTO.builder()
                .id(media.getId())
                .mediaUrl(media.getMediaUrl())
                .mediaType(media.getMediaType().name())

                .messageSentAt(message.getSentAt())

                .fullName(sender.getFullName())
                .avatarUrl(sender.getAvatarUrl())
                .build();



    }


    // lấy danh sách file hay ảnh của bạn trong cuộc trog chuyện





}
