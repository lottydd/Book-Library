package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.dto.response.user.UserDTO;
import com.example.booklibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //+
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO registeredUser = userService.registerUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
    //+ нужен наверное метод чтобы убрать роль еще
    @PostMapping("/{userId}/assign-role/{roleName}")
    public ResponseEntity<UserDTO> assignRoleToUser(
            @PathVariable int userId,
            @PathVariable String roleName) {
        UserDTO updatedUser = userService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok(updatedUser);
    }

    // +
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable int userId,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }
    // +
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        userService.deleteUser(new RequestIdDTO(userId));
        return ResponseEntity.noContent().build();
    }
    //+
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable int userId) {
        UserDTO user = userService.findUserById(new RequestIdDTO(userId));
        return ResponseEntity.ok(user);
    }

}