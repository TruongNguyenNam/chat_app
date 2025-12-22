package com.example.chatappzalo.service.user.impl;

import com.example.chatappzalo.core.chatapp.user.payload.UserResponseDTO;
import com.example.chatappzalo.entity.User;
import com.example.chatappzalo.infrastructure.utils.SecurityUtils;
import com.example.chatappzalo.repositories.UserRepository;
import com.example.chatappzalo.service.user.FriendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserResponseDTO findByPhone(String phone) {
        Long currentId = SecurityUtils.getCurrentUserId();
      User user = userRepository.findUserByPhoneNoContact(phone,currentId).orElseThrow(
              () -> new IllegalArgumentException("không tìm thấy được người này")
      );
            return modelMapper.map(user, UserResponseDTO.class);

    }


}
