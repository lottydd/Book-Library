package com.example.booklibrary.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO с информацией о пользователе")

public class UserDTO {
    @Schema(description = "ID пользователя", example = "1")
    private Integer id;

    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;

    @Schema(description = "Email пользователя", example = "john@example.com")
    private String email;

    @Schema(description = "Список ролей пользователя", example = "[\"USER\", \"ADMIN\"]")
    private List<String> roles;
}