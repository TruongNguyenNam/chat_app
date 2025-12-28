package com.example.chatappzalo.service.message_reactions.impl;

import com.example.chatappzalo.core.chatapp.message_reactions.payload.MessageReactionsRequestDTO;
import com.example.chatappzalo.core.chatapp.message_reactions.payload.MessageReactionsResponseDTO;
import com.example.chatappzalo.entity.Message;
import com.example.chatappzalo.entity.MessageReactions;
import com.example.chatappzalo.entity.User;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.MessageReactionsRepository;
import com.example.chatappzalo.repositories.MessageRepository;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.message_reactions.MessageReactionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReactionServiceImpl implements MessageReactionService {
    private final MessageReactionsRepository messageReactionsRepository;

    private final UserRepository userRepository;

    private final MessageRepository messageRepository;


    @Override
    @Transactional
    public void sendMessageReaction(MessageReactionsRequestDTO requestDTO) {
        MessageReactions reactions = new MessageReactions();
        Long currentId = SecurityUtils.getCurrentUserId();
//        log.info("");
        reactions.setReaction(requestDTO.getReaction());
        Message message = messageRepository.findById(requestDTO.getMessageId()).orElseThrow(
                () -> new IllegalArgumentException("không tìm thấy nội dung đoạn chat này")
        );
        reactions.setMessage(message);
        User user = userRepository.findById(currentId).orElseThrow(() ->
                new IllegalArgumentException("không tìm thấy người gửi"));
        reactions.setUser(user);
        messageReactionsRepository.save(reactions);
    }


    @Override
    @Transactional
    public MessageReactionsResponseDTO getMessageReactions(Long messageId) {
        List<Object[]> results = messageReactionsRepository.countByReaction(messageId);

        List<MessageReactionsResponseDTO.CountMessageReactionsDTO> reactions =
                results.stream()
                        .map(this::mapToCountReaction)
                        .toList();

        return MessageReactionsResponseDTO.builder()
                .messageId(messageId)
                .reactions(reactions)
                .build();
    }

    private MessageReactionsResponseDTO.CountMessageReactionsDTO mapToCountReaction(Object[] row) {
        // row[0] = reaction, row[1] = count
        Long messageReactionId = ((Number) row[0]).longValue();
        String reaction = String.valueOf(row[1]);
        Long total = ((Number) row[2]).longValue(); // an toàn với Long, Integer, BigInteger

        return MessageReactionsResponseDTO.CountMessageReactionsDTO.builder()
                .MessageReactionId(messageReactionId)
                .reaction(reaction)
                .totalReactions(total)
                .build();
    }


}
