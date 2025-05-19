package com.example.booklibrary.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserPasswordChangeDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.dto.response.user.UserDTO;
import com.example.booklibrary.service.UserService;
import org.springframework.security.test.context.support.WithMockUser;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserCreateDTO userCreateDTO;
    private UserDTO userDTO;
    private UserUpdateDTO userUpdateDTO;
    private UserPasswordChangeDTO passwordChangeDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("email@example.com");
        userCreateDTO.setUsername("username");
        userCreateDTO.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setEmail("email@example.com");
        userDTO.setUsername("username");

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setUsername("updatedUsername");

        passwordChangeDTO = new UserPasswordChangeDTO();
        passwordChangeDTO.setNewPassword("newPassword123");
    }

    @Test
    void registerUser_success() {
        when(userService.registerUser(userCreateDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.registerUser(userCreateDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).registerUser(userCreateDTO);
    }

    @Test
    void registerUser_fail() {
        when(userService.registerUser(userCreateDTO))
                .thenThrow(new IllegalArgumentException("Email or username already exists"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userController.registerUser(userCreateDTO);
        });

        assertEquals("Email or username already exists", ex.getMessage());
        verify(userService, times(1)).registerUser(userCreateDTO);
    }

    @Test
    void assignRoleToUser_success() {
        when(userService.assignRoleToUser(1, "ADMIN")).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.assignRoleToUser(1, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).assignRoleToUser(1, "ADMIN");
    }

    @Test
    void assignRoleToUser_fail() {
        when(userService.assignRoleToUser(1, "ADMIN"))
                .thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            userController.assignRoleToUser(1, "ADMIN");
        });

        assertEquals("User not found", ex.getMessage());
        verify(userService, times(1)).assignRoleToUser(1, "ADMIN");
    }

    @Test
    void deleteRoleFromUser_success() {
        when(userService.deleteRoleFromUser(1, "USER")).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.deleteRoleFromUser(1, "USER");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).deleteRoleFromUser(1, "USER");
    }

    @Test
    void deleteRoleFromUser_fail() {
        when(userService.deleteRoleFromUser(1, "USER"))
                .thenThrow(new EntityNotFoundException("User or role not found"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            userController.deleteRoleFromUser(1, "USER");
        });

        assertEquals("User or role not found", ex.getMessage());
        verify(userService, times(1)).deleteRoleFromUser(1, "USER");
    }

    @Test
    void updateUser_success() {
        when(userService.updateUser(1, userUpdateDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(1, userUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).updateUser(1, userUpdateDTO);
    }

    @Test
    void updateUser_fail() {
        when(userService.updateUser(1, userUpdateDTO))
                .thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            userController.updateUser(1, userUpdateDTO);
        });

        assertEquals("User not found", ex.getMessage());
        verify(userService, times(1)).updateUser(1, userUpdateDTO);
    }

    @Test
    void changePassword_success() {
        doNothing().when(userService).changePassword(1, passwordChangeDTO.getNewPassword());

        ResponseEntity<Void> response = userController.changePassword(1, passwordChangeDTO);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).changePassword(1, passwordChangeDTO.getNewPassword());
    }

    @Test
    void changePassword_fail() {
        doThrow(new EntityNotFoundException("User not found"))
                .when(userService).changePassword(1, passwordChangeDTO.getNewPassword());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            userController.changePassword(1, passwordChangeDTO);
        });

        assertEquals("User not found", ex.getMessage());
        verify(userService, times(1)).changePassword(1, passwordChangeDTO.getNewPassword());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_success() {
        ResponseEntity<Void> response = userController.deleteUser(1);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(userService).deleteUser(refEq(new RequestIdDTO(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_fail() {
        doThrow(EntityNotFoundException.class)
                .when(userService).deleteUser(refEq(new RequestIdDTO(1)));

        assertThrows(EntityNotFoundException.class, () -> userController.deleteUser(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void getUserInfo_success() {
        UserDTO expected = new UserDTO();
        expected.setId(1);
        expected.setUsername("testUser");

        when(userService.findUserById(refEq(new RequestIdDTO(1))))
                .thenReturn(expected);

        ResponseEntity<UserDTO> response = userController.getUserInfo(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void getUserInfo_fail() {
        when(userService.findUserById(refEq(new RequestIdDTO(1))))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userController.getUserInfo(1));
    }
}
