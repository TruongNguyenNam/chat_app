package com.example.chatappzalo.service.contact;

import com.example.chatappzalo.core.chatapp.contact.payload.ContactRequestDTO;
import com.example.chatappzalo.core.chatapp.contact.payload.ContactResponseDTO;
import com.example.chatappzalo.entity.Contact;

import java.util.List;

public interface ContactService {

     List<ContactResponseDTO> findByFriends();

     void addFriend(ContactRequestDTO contactRequestDTO);

     List<ContactResponseDTO> getPendingRequests();

     void rejectFriendRequest(Long contactId);

     void acceptedFriendRequest(Long contactId);

      Long acceptedFriendRequestV2(Long contactId);

}
