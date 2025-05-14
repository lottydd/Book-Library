package com.example.booklibrary.dto.request.user;

import com.example.booklibrary.model.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @NotBlank(message = "Username не может быть пустым")
    private String username;
    @NotBlank(message = "Email  не может быть пустым")
    @Email
    private String email;
}
