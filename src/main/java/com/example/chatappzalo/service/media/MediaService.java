package com.example.chatappzalo.service.media;

import com.example.chatappzalo.core.chatapp.media.payload.MediaResponseDTO;
import com.example.chatappzalo.entity.Media;

import java.util.List;

public interface MediaService {

    List<MediaResponseDTO> findByChatID(Long chatId, List<Media.MediaType> mediaType);


}
