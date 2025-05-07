package com.example.booklibrary.dao;

import com.example.booklibrary.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public class UserDAO extends BaseDAO<User, Integer> {
    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByEmail(String email) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsByEmailOrUsername(String email, String username) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email OR u.username = :username",
                        Long.class)
                .setParameter("email", email)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
}
