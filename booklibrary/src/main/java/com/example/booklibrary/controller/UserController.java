package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserPasswordChangeDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.dto.response.user.UserDTO;
import com.example.booklibrary.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description ="управление пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя в системе")

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO registeredUser = userService.registerUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @Operation(summary = "Назначение роли пользователю", description = "Добавляет указанную роль пользователю (только для ADMIN)")

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/assign-role/{roleName}")
    public ResponseEntity<UserDTO> assignRoleToUser(
            @Parameter(description = "ID пользователя", example = "1")    @PathVariable int userId,
            @Parameter(description = "Название роли", example = "ADMIN")  @PathVariable String roleName) {
        UserDTO updatedUser = userService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Удаление роли у пользователя", description = "Удаляет указанную роль у пользователя (только для ADMIN)")

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/delete-role/{roleName}")
    public ResponseEntity<UserDTO> deleteRoleFromUser(
            @Parameter(description = "ID пользователя", example = "1")   @PathVariable int userId,
            @Parameter(description = "Название роли", example = "ADMIN")  @PathVariable String roleName) {
        UserDTO updatedUser = userService.deleteRoleFromUser(userId, roleName);
        return ResponseEntity.ok(updatedUser);
    }


    @Operation(summary = "Обновление данных пользователя", description = "Обновляет основную информацию о пользователе (доступно USER и ADMIN)")

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID пользователя", example = "1")     @PathVariable int userId,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @Operation(summary = "Смена пароля", description = "Изменяет пароль пользователя (доступно USER и ADMIN)")

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "ID пользователя", example = "1")    @PathVariable int userId,
            @RequestBody @Valid UserPasswordChangeDTO passwordChangeDTO) {
        userService.changePassword(userId, passwordChangeDTO.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя из системы (только для ADMIN)")

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(  @Parameter(description = "ID пользователя", example = "1") @PathVariable int userId) {
        userService.deleteUser(new RequestIdDTO(userId));
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Получение информации о пользователе", description = "Возвращает полную информацию о пользователе (только для ADMIN)")

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("user-info/{userId}")
    public ResponseEntity<UserDTO> getUserInfo(  @Parameter(description = "ID пользователя", example = "1") @PathVariable int userId) {
        UserDTO user = userService.findUserById(new RequestIdDTO(userId));
        return ResponseEntity.ok(user);
    }

}