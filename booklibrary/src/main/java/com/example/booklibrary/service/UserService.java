package com.example.booklibrary.service;

import com.example.booklibrary.dao.RoleDAO;
import com.example.booklibrary.dao.UserDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.dto.response.user.UserDTO;
import com.example.booklibrary.mapper.UserMapper;
import com.example.booklibrary.model.Role;
import com.example.booklibrary.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserDAO userDAO, RoleDAO roleDAO, UserMapper userMapper) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDTO registerUser(UserCreateDTO userCreateDTO) {
        logger.debug("Попытка регистрации нового пользователя. Email: {}, Username: {}",
                userCreateDTO.getEmail(), userCreateDTO.getUsername());

        validateRegistrationData(userCreateDTO);
        User user = userMapper.toEntity(userCreateDTO);
        User savedUser = userDAO.save(user);
        assignRoleToUser(savedUser.getId(), "USER");

        logger.info("Пользователь успешно зарегистрирован. UserID: {}, Email: {}",
                savedUser.getId(), savedUser.getEmail());
        return userMapper.toDto(savedUser);
    }


    @Transactional
    public UserDTO assignRoleToUser(int userId, String roleName) {
        logger.debug("Попытка назначения роли пользователю. UserID: {}, Role: {}", userId, roleName);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("User not found");
                });

        Role role = roleDAO.findRoleName(roleName)
                .orElseThrow(() -> {
                    logger.warn("Роль не найдена. Role: {}", roleName);
                    return new EntityNotFoundException("Role not found");
                });

        user.getRoles().add(role);
        userDAO.save(user);
        logger.info("Роль успешно назначена. UserID: {}, Role: {}", userId, roleName);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO updateUser(int userId, UserUpdateDTO userUpdateDTO) {
        logger.debug("Попытка обновления пользователя. UserID: {}", userId);

        validateUpdateData(userUpdateDTO, userId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("Пользователь не найден");
                });

        userMapper.updateFromDto(userUpdateDTO, user);

        User updatedUser = userDAO.save(user);

        logger.info("Данные пользователя обновлены. UserID: {}", userId);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(RequestIdDTO dto) {
        logger.debug("Попытка удаления пользователя. UserID: {}",dto.getId() );

        User user = userDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден при попытке удаления. UserID: {}", dto.getId());
                    return new EntityNotFoundException("Пользователь не найден");
                });

        if (!user.getRentals().isEmpty()) {
            logger.warn("Попытка удаления пользователя с активными арендами. UserID: {}", dto.getId());
            throw new IllegalStateException("Нельзя удалить пользователя с активными арендами");
        }

        userDAO.delete(dto.getId());
        logger.info("Пользователь успешно удален. UserID: {}", dto.getId());
    }

    @Transactional(readOnly = true)
    public UserDTO findUserById(RequestIdDTO dto) {
        logger.debug("Поиск пользователя по ID. UserID: {}", dto.getId());

        return userMapper.toDto(userDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден.   UserID: {}", dto.getId());
                    return new EntityNotFoundException("User not found");
                }));
    }


    @Transactional(readOnly = true)
    private void validateRegistrationData(UserCreateDTO dto) {
        logger.debug("Валидация данных регистрации пользователя");
        if (dto == null) {
            logger.warn("Попытка регистрации с null данными");
            throw new IllegalArgumentException("Попытка регистрации с null данными");
        }

        if (userDAO.existsByEmailOrUsername(dto.getEmail(), dto.getUsername())) {
            logger.warn("Попытка регистрации с занятым email или username. Email: {}, Username: {}",
                    dto.getEmail(), dto.getUsername());
            throw new IllegalArgumentException("Email или username уже заняты");
        }
        logger.debug("Данные регистрации валидны");
    }

    @Transactional(readOnly = true)
    private void validateUpdateData(UserUpdateDTO dto, Integer currentUserId) {
        logger.debug("Валидация данных обновления пользователя. UserID: {}", currentUserId);
        if (dto == null) {
            logger.warn("Попытка обновления с null данными");
            throw new IllegalArgumentException("Попытка обновления с null данными");
        }

        userDAO.findByEmail(dto.getEmail())
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.warn("Попытка обновления на занятый email. Email: {}, CurrentUserID: {}",
                            dto.getEmail(), currentUserId);
                    throw new IllegalArgumentException("Email " + dto.getEmail() + " уже занят другим пользователем");
                });

        userDAO.findByUsername(dto.getUsername())
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.warn("Попытка обновления на занятый username. Username: {}, CurrentUserID: {}",
                            dto.getUsername(), currentUserId);
                    throw new IllegalArgumentException("Username " + dto.getUsername() + " уже занят другим пользователем");
                });
        logger.debug("Данные обновления валидны");
    }


}
