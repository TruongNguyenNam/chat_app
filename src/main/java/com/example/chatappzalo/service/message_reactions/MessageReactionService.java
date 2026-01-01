package com.example.chatappzalo.service.message_reactions;

import com.example.chatappzalo.core.chatapp.message_reactions.payload.MessageReactionsRequestDTO;
import com.example.chatappzalo.core.chatapp.message_reactions.payload.MessageReactionsResponseDTO;
import com.example.chatappzalo.entity.MessageReactions;

import java.util.List;

public interface MessageReactionService {

    void sendMessageReaction(MessageReactionsRequestDTO messageReactionsRequestDTO);



     MessageReactionsResponseDTO getMessageReactions(Long messageId);



}
