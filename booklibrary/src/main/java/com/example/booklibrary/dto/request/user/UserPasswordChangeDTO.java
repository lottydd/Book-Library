package com.example.booklibrary.dto.request.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос  для изменения пароля пользователя")
public class UserPasswordChangeDTO {

    @Schema(description = "Новый пароль", example = "newPassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String newPassword;
}
