package com.example.booklibrary.dto.request.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос для создания нового пользователя")

public class UserCreateDTO {

    @Schema(description = "Имя пользователя", example = "johnCoolGuy", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 5, max = 16)
    @NotBlank(message = "Username не может быть пустым")
    private String username;


    @Schema(description = "Email пользователя", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат почты ")
    private String email;
    @Size(min = 8, max = 32)
    @Schema(description = "Пароль пользователя", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}