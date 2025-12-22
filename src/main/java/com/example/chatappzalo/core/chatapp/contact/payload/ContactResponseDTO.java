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
public class ContactResponseDTO {
    private Long id;
    private String fullName;
    private String gender;
    private String avatarUrl;
    private String email;
    private String phone;
    private Long friendId;
    private Long chatId;
    private String ContactType ;


}
