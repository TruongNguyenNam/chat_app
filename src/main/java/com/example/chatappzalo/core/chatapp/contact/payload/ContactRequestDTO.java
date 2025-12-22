package com.example.chatappzalo.core.chatapp.contact.payload;

import com.example.chatappzalo.entity.Contact;
import com.example.chatappzalo.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDTO {


    private Long userId;

    private Long friendId;



}
