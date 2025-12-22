package com.example.chatappzalo.core.auth.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
public class loginForm {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
