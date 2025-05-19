package com.example.booklibrary.service;

import com.example.booklibrary.dao.RoleDAO;
import com.example.booklibrary.dao.UserDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserPasswordChangeDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.dto.response.user.UserDTO;
import com.example.booklibrary.mapper.UserMapper;
import com.example.booklibrary.model.Role;
import com.example.booklibrary.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private RoleDAO roleDAO;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateDTO userCreateDTO;
    private User userEntity;
    private UserDTO userDTO;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userCreateDTO = new UserCreateDTO("username", "email@example.com", "password");
        userEntity = new User();
        userEntity.setId(1);
        userEntity.setUsername("username");
        userEntity.setEmail("email@example.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setRoles(new ArrayList<>());

        userRole = new Role();
        userRole.setRoleName("ROLE_USER");

        userDTO = new UserDTO(1, "username", "email@example.com", List.of("ROLE_USER"));
    }

    @Test
    void registerUser_success() {
        when(userDAO.existsByEmailOrUsername(userCreateDTO.getEmail(), userCreateDTO.getUsername())).thenReturn(false);
        when(userMapper.toEntity(userCreateDTO)).thenReturn(userEntity);
        when(passwordEncoder.encode(userCreateDTO.getPassword())).thenReturn("encodedPassword");
        when(userDAO.save(any(User.class))).thenReturn(userEntity);
        when(roleDAO.findRoleName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userMapper.toDto(userEntity)).thenReturn(userDTO);
        when(userDAO.findById(anyInt())).thenReturn(Optional.of(userEntity));

        UserDTO result = userService.registerUser(userCreateDTO);

        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        verify(userDAO, times(2)).save(userEntity);
        verify(roleDAO).findRoleName("ROLE_USER");
    }

    @Test
    void registerUser_emailOrUsernameExists_throwsException() {
        when(userDAO.existsByEmailOrUsername(userCreateDTO.getEmail(), userCreateDTO.getUsername())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(userCreateDTO));
        assertEquals("Email или username уже заняты", ex.getMessage());
        verify(userDAO, never()).save(any());
    }

    @Test
    void assignRoleToUser_success() {
        User user = new User();
        user.setId(1);
        user.setRoles(new ArrayList<>());

        Role role = new Role();
        role.setRoleName("ROLE_ADMIN");

        when(userDAO.findById(1)).thenReturn(Optional.of(user));
        when(roleDAO.findRoleName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        UserDTO result = userService.assignRoleToUser(1, "ROLE_ADMIN");

        assertTrue(user.getRoles().contains(role));
        assertEquals(userDTO, result);
        verify(userDAO).save(user);
    }

    @Test
    void assignRoleToUser_userNotFound_throwsException() {
        when(userDAO.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.assignRoleToUser(1, "ROLE_ADMIN"));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void deleteRoleFromUser_success() {
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        User user = new User();
        user.setId(1);
        user.setRoles(new ArrayList<>(List.of(role)));

        when(userDAO.findById(1)).thenReturn(Optional.of(user));
        when(roleDAO.findRoleName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        UserDTO result = userService.deleteRoleFromUser(1, "ROLE_USER");

        assertFalse(user.getRoles().contains(role));
        assertEquals(userDTO, result);
    }

    @Test
    void deleteRoleFromUser_roleNotFound_throwsException() {
        User user = new User();
        user.setId(1);
        user.setRoles(new ArrayList<>());

        when(userDAO.findById(1)).thenReturn(Optional.of(user));
        when(roleDAO.findRoleName("NON_EXISTENT")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.deleteRoleFromUser(1, "NON_EXISTENT"));
        assertEquals("Role not found", ex.getMessage());
    }

    @Test
    void updateUser_success() {
        UserUpdateDTO updateDTO = new UserUpdateDTO("newUsername", "newemail@example.com");
        User user = new User();
        user.setId(1);
        user.setUsername("oldUsername");
        user.setEmail("oldemail@example.com");

        when(userDAO.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateFromDto(updateDTO, user);
        when(userDAO.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);
        when(userDAO.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        when(userDAO.findByUsername(updateDTO.getUsername())).thenReturn(Optional.empty());

        UserDTO result = userService.updateUser(1, updateDTO);

        assertEquals(userDTO, result);
        verify(userDAO).save(user);
    }

    @Test
    void updateUser_userNotFound_throwsException() {
        UserUpdateDTO updateDTO = new UserUpdateDTO("newUsername", "newemail@example.com");
        when(userDAO.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1, updateDTO));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        user.setId(1);
        user.setRentals(Collections.emptyList());

        when(userDAO.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(userDAO).delete(1);

        assertDoesNotThrow(() -> userService.deleteUser(new RequestIdDTO(1)));
        verify(userDAO).delete(1);
    }

    @Test
    void deleteUser_withActiveRentals_throwsException() {
        User user = new User();
        user.setId(1);
        user.setRentals(List.of(new com.example.booklibrary.model.Rental() {{
            setStatus(com.example.booklibrary.util.RentalStatus.RENTED);
        }}));

        when(userDAO.findById(1)).thenReturn(Optional.of(user));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.deleteUser(new RequestIdDTO(1)));
        assertEquals("Нельзя удалить пользователя с активными арендами", ex.getMessage());
    }

    @Test
    void deleteUser_userNotFound_throwsEntityNotFoundException() {
        int userId = 123;
        RequestIdDTO dto = new RequestIdDTO(userId);

        when(userDAO.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(dto);
        });

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userDAO).findById(userId);
        verify(userDAO, never()).delete(anyInt());
    }

    @Test
    void findUserById_success() {
        when(userDAO.findById(1)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.findUserById(new RequestIdDTO(1));
        assertEquals(userDTO, result);
    }

    @Test
    void findUserById_notFound_throwsException() {
        when(userDAO.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(new RequestIdDTO(1)));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void changePassword_success() {
        User user = new User();
        user.setId(1);
        when(userDAO.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        assertDoesNotThrow(() -> userService.changePassword(1, "newPass"));
        verify(userDAO).update(user);
        assertEquals("encodedPass", user.getPassword());
    }

    @Test
    void changePassword_userNotFound_throwsException() {
        when(userDAO.findById(1)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.changePassword(1, "newPass"));
        assertEquals("User not found", ex.getMessage());
    }
}
