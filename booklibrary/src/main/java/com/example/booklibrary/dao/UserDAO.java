package com.example.booklibrary.dao;

import com.example.booklibrary.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public class UserDAO extends BaseDAO<User, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByEmail(String email) {
        logger.debug("Поиск Пользователя по email {}", email);
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();

            logger.info("Пользователь с email {} найден. Имя пользователя: {}, ID пользователя {} ",
                    email,
                    user.getUsername(),
                    user.getId());
            return Optional.of(user);
        } catch (NoResultException e) {
            logger.error("Пользователь с email {} не найден", email);
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        logger.debug("Поиск Пользователя по username {}", username);
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            logger.info("Пользователь с username {} найден. Email пользователя: {}, ID пользователя {} ",
                    username,
                    user.getEmail(),
                    user.getId());
            return Optional.of(user);
        } catch (NoResultException e) {
            logger.error("Пользователь с username {} не найден", username);
            return Optional.empty();
        }
    }

    public boolean existsByEmailOrUsername(String email, String username) {
        logger.debug("Проверка существования пользователя с username {} и email{}", username, email);
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email OR u.username = :username",
                        Long.class)
                .setParameter("email", email)
                .setParameter("username", username)
                .getSingleResult();
        if (count!=0){
            logger.info("Пользователь с username {} и email{} существует", username, email);
        }
        return count > 0;
    }
}
