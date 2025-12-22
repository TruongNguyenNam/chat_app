package com.example.chatappzalo.core.chatapp.contact.controller;

import com.example.chatappzalo.core.chatapp.contact.payload.ContactRequestDTO;
import com.example.chatappzalo.core.chatapp.contact.payload.ContactResponseDTO;
import com.example.chatappzalo.core.chatapp.message.payload.MessageResponseDTO;
import com.example.chatappzalo.entity.Contact;
import com.example.chatappzalo.infrastructure.utils.ResponseData;
import com.example.chatappzalo.service.contact.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contact")
@Validated
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseData<List<ContactResponseDTO>> findByFriends() {
        List<ContactResponseDTO> contactResponseDTOS = contactService.findByFriends();

        return ResponseData.<List<ContactResponseDTO>>builder()
                .status(200)
                .message("danh sách bạn bè thành công")
                .data(contactResponseDTOS)
                .build();
    }

    

    @PostMapping("/send")
    public ResponseData<Void> getPendingRequests(@RequestBody ContactRequestDTO contactRequestDTO) {
        contactService.addFriend(contactRequestDTO);

        return ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Gửi lời mời kết bạn thành công")
                .build();
    }

    @GetMapping("/pending")
    public ResponseData<List<ContactResponseDTO>> getPendingRequests() {

        List<ContactResponseDTO> pendingRequests = contactService.getPendingRequests();

        return ResponseData.<List<ContactResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách lời mời kết bạn thành công")
                .data(pendingRequests)
                .build();
    }


    @PostMapping("/reject/{contactId}")
    public ResponseData<Void> rejectFriendRequest(
            @PathVariable Long contactId
    ) {
        contactService.rejectFriendRequest(contactId);

        return ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Từ chối lời mời kết bạn thành công")
                .build();
    }

    @PostMapping("/accepted/{contactId}")
    public ResponseData<Void> acceptedFriendRequest(
            @PathVariable Long contactId
    ) {
        contactService.acceptedFriendRequest(contactId);

        return ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("xác nhận lời mời kết bạn thành công")
                .build();
    }


}
