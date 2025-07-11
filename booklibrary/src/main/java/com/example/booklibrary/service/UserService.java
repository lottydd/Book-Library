package com.example.booklibrary.service;

import com.example.booklibrary.dao.RoleDAO;
import com.example.booklibrary.dao.UserDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.dto.response.user.UserDTO;
import com.example.booklibrary.mapper.UserMapper;
import com.example.booklibrary.model.Rental;
import com.example.booklibrary.model.Role;
import com.example.booklibrary.model.User;
import com.example.booklibrary.util.RentalStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserDAO userDAO, RoleDAO roleDAO, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO registerUser(UserCreateDTO userCreateDTO) {
        logger.info("Попытка регистрации нового пользователя. Email: {}, Username: {}",
                userCreateDTO.getEmail(), userCreateDTO.getUsername());

        validateRegistrationData(userCreateDTO);
        User user = userMapper.toEntity(userCreateDTO);
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        User savedUser = userDAO.save(user);
        assignRoleToUser(savedUser.getId(), "ROLE_USER");

        logger.info("Пользователь успешно зарегистрирован. UserID: {}, Email: {}",
                savedUser.getId(), savedUser.getEmail());
        return userMapper.toDto(savedUser);
    }

    private boolean validationRoleDuplication(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(existingRole -> existingRole.getRoleName().equalsIgnoreCase(roleName));
    }

    @Transactional
    public UserDTO assignRoleToUser(int userId, String roleName) {
        logger.info("Попытка назначения роли пользователю. UserID: {}, Role: {}", userId, roleName);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("User not found");
                });

        Role role = roleDAO.findRoleName(roleName)
                .orElseThrow(() -> {
                    logger.error("Роль не найдена. Role: {}", roleName);
                    return new EntityNotFoundException("Role not found " + roleName);
                });

        if (validationRoleDuplication(user, role.getRoleName())) {
            logger.error("Пользователь уже имеет роль. UserID: {}, Role: {}", userId, roleName);
            throw new IllegalArgumentException("Пользователь уже имеет такую роль");
        }
        user.getRoles().add(role);
        userDAO.save(user);
        logger.info("Роль успешно назначена. UserID: {}, Role: {}", userId, roleName);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO deleteRoleFromUser(int userId, String roleName) {
        logger.info("Попытка удаления роли у пользователя. UserID: {}, Role: {}", userId, roleName);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("User not found");
                });

        Role role = roleDAO.findRoleName(roleName)
                .orElseThrow(() -> {
                    logger.error("Роль не найдена. Role: {}", roleName);
                    return new EntityNotFoundException("Role not found");
                });

        if (!user.getRoles().contains(role)) {
            logger.error("У пользователя нет такой роли. UserID: {}, Role: {}", userId, roleName);
            throw new IllegalArgumentException("У пользователя нет такой роли");
        }

        user.getRoles().remove(role);
        logger.info("Роль удалена у пользователя. UserID: {}, Role: {}", userId, roleName);

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO updateUser(int userId, UserUpdateDTO userUpdateDTO) {
        logger.info("Попытка обновления пользователя. UserID: {}", userId);

        validateUpdateData(userUpdateDTO, userId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("Пользователь не найден");
                });

        userMapper.updateFromDto(userUpdateDTO, user);

        User updatedUser = userDAO.save(user);

        logger.info("Данные пользователя обновлены. UserID: {}", userId);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(RequestIdDTO dto) {
        logger.info("Попытка удаления пользователя. UserID: {}", dto.getId());

        User user = userDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден при попытке удаления. UserID: {}", dto.getId());
                    return new EntityNotFoundException("Пользователь не найден");
                });

        if (validateUserRentals(user.getRentals())) {
            logger.error("Попытка удаления пользователя с активными арендами. UserID: {}", dto.getId());
            throw new IllegalStateException("Нельзя удалить пользователя с активными арендами");
        }

        userDAO.delete(dto.getId());
        logger.info("Пользователь успешно удален. UserID: {}", dto.getId());
    }

    private boolean validateUserRentals(List<Rental> rentals) {
        return rentals.stream()
                .anyMatch(rental -> rental.getStatus() == RentalStatus.RENTED || rental.getStatus() == RentalStatus.LATE);
    }


    @Transactional(readOnly = true)
    public UserDTO findUserById(RequestIdDTO dto) {
        logger.info("Поиск пользователя по ID. UserID: {}", dto.getId());

        return userMapper.toDto(userDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден.   UserID: {}", dto.getId());
                    return new EntityNotFoundException("User not found");
                }));
    }

    @Transactional(readOnly = true)
    private void validateRegistrationData(UserCreateDTO dto) {
        logger.info("Валидация данных регистрации пользователя");
        if (dto == null) {
            logger.error("Попытка регистрации с null данными");
            throw new IllegalArgumentException("Попытка регистрации с null данными");
        }

        if (userDAO.existsByEmailOrUsername(dto.getEmail(), dto.getUsername())) {
            logger.error("Попытка регистрации с занятым email или username. Email: {}, Username: {}",
                    dto.getEmail(), dto.getUsername());
            throw new IllegalArgumentException("Email или username уже заняты");
        }
        logger.info("Данные регистрации валидны");
    }

    @Transactional(readOnly = true)
    private void validateUpdateData(UserUpdateDTO dto, Integer currentUserId) {
        logger.info("Валидация данных обновления пользователя. UserID: {}", currentUserId);
        if (dto == null) {
            logger.error("Попытка обновления с null данными");
            throw new IllegalArgumentException("Попытка обновления с null данными");
        }

        userDAO.findByEmail(dto.getEmail())
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.error("Попытка обновления на занятый email. Email: {}, CurrentUserID: {}",
                            dto.getEmail(), currentUserId);
                    throw new IllegalArgumentException("Email " + dto.getEmail() + " уже занят другим пользователем");
                });

        userDAO.findByUsername(dto.getUsername())
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.error("Попытка обновления на занятый username. Username: {}, CurrentUserID: {}",
                            dto.getUsername(), currentUserId);
                    throw new IllegalArgumentException("Username " + dto.getUsername() + " уже занят другим пользователем");
                });
        logger.info("Данные обновления валидны");
    }

    @Transactional
    public void changePassword(int userId, String newPassword) {
        logger.info("Попытка смены пароля пользователя");
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        logger.info("Пользователь с ID {} найден", user.getId());
        user.setPassword(passwordEncoder.encode(newPassword));
        userDAO.update(user);
    }
}
