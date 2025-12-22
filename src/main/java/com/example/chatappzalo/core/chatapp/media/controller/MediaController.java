package com.example.chatappzalo.core.chatapp.media.controller;

import com.example.chatappzalo.core.chatapp.media.payload.MediaResponseDTO;
import com.example.chatappzalo.entity.Media;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.media.MediaService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
@Validated
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;


    @GetMapping("/{chatId}")
    public ResponseData<List<MediaResponseDTO>> findByChatId(
            @PathVariable Long chatId,
            @RequestParam(required = false) Media.MediaType mediaType
    ) {

        List<Media.MediaType> mediaTypes;

        if (mediaType != null) {
            mediaTypes = List.of(mediaType);
        } else {
            mediaTypes = List.of(
                    Media.MediaType.IMAGE,
                    Media.MediaType.VIDEO,
                    Media.MediaType.FILE,
                    Media.MediaType.VOICE
            );
        }

        List<MediaResponseDTO> result =
                mediaService.findByChatID(chatId, mediaTypes);

        return ResponseData.<List<MediaResponseDTO>>builder()
                .data(result)
                .status(200)
                .message("lấy danh file ảnh thành công")
                .build();
    }






}
