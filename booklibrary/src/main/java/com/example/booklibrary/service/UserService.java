package com.example.booklibrary.service;

import com.example.booklibrary.dao.RoleDAO;
import com.example.booklibrary.dao.UserDAO;
import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.mapper.UserMapper;
import com.example.booklibrary.model.Role;
import com.example.booklibrary.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final UserMapper userMapper;

    public UserService(UserDAO userDAO, RoleDAO roleDAO, UserMapper userMapper) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    private void validateRegistrationData(UserCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Registration data cannot be null");

        if (userDAO.existsByEmailOrUsername(dto.getEmail(), dto.getUsername())) {
            throw new IllegalArgumentException("Email or username already taken");
        }
    }

    @Transactional(readOnly = true)
    private void validateUpdateData(UserUpdateDTO dto, Integer currentUserId) {
        if (dto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        userDAO.findByEmail(dto.getEmail())
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email " + dto.getEmail() + " is already taken by another user");
                });

        userDAO.findByUsername(dto.getUsername())
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username " + dto.getUsername() + " is already taken by another user");
                });
    }


    @Transactional
    public void registerUser(UserCreateDTO userCreateDTO) {
        validateRegistrationData(userCreateDTO);
        User user = userMapper.toEntity(userCreateDTO);
        userDAO.save(user);
        assignRoleToUser(user.getId(), "USER");
    }


    @Transactional
    public void assignRoleToUser(int userId, String roleName) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Role role = roleDAO.findRoleName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        user.getRoles().add(role);
//        userDAO.update(user);
    }


    @Transactional
    public void updateUser(int userId, UserUpdateDTO userUpdateDTO) {
        validateUpdateData(userUpdateDTO, userId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateFromDto(userUpdateDTO, user);
    }

    @Transactional
    public void deleteUser(int userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getRentals().isEmpty()) {
            throw new IllegalStateException("Cannot delete user with active rentals");
        }

        userDAO.delete(userId);
    }

    @Transactional(readOnly = true)
    public User findUserById(int userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

}
