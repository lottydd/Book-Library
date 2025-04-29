package com.example.booklibrary.dto.request.user;

import com.example.booklibrary.model.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @NotBlank
    @Nullable
    private String username;
    @NotBlank
    @Nullable
    @Email
    private String email;
    @NotBlank
    @Nullable
    private String password;

}
