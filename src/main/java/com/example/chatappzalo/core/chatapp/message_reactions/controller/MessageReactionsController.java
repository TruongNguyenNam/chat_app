package com.example.chatappzalo.core.chatapp.message_reactions.controller;

import com.example.chatappzalo.core.chatapp.message.payload.MessageResponseDTO;
import com.example.chatappzalo.core.chatapp.message_reactions.payload.MessageReactionsRequestDTO;
import com.example.chatappzalo.core.chatapp.message_reactions.payload.MessageReactionsResponseDTO;
import com.example.chatappzalo.entity.MessageReactions;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.message_reactions.MessageReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/message_reactions")
@Validated
@Slf4j
@RequiredArgsConstructor
public class MessageReactionsController {

    private final MessageReactionService messageReactionService;


    @GetMapping("/{messageId}")
    public ResponseData<MessageReactionsResponseDTO> getMessageReactions(
            @PathVariable Long messageId
    ) {

        MessageReactionsResponseDTO response =
                messageReactionService.getMessageReactions(messageId);

        return
                new ResponseData<>(
                        HttpStatus.OK.value(),
                        "Lấy reaction của message thành công",
                        response

                );
    }


    @PostMapping
    public ResponseData<Void> sendMessageReactions(@RequestBody MessageReactionsRequestDTO requestDTO){
        messageReactionService.sendMessageReaction(requestDTO);
          return ResponseData.<Void>builder()
                .status(200)
                .message("gửi reaction thành công")
                .build();

    }



}
