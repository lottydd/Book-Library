package com.example.booklibrary.dao;


import com.example.booklibrary.model.Role;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Repository
public class RoleDAO extends BaseDAO<Role, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(RoleDAO.class);

    public RoleDAO() {
        super(Role.class);
    }

    public Optional<Role> findRoleName(String roleName) {
        logger.info("Поиск роли по имени {}", roleName);
        try {
            Role role = entityManager.createQuery(
                            "SELECT r FROM Role r WHERE r.roleName = :roleName",
                            Role.class
                    )
                    .setParameter("roleName", roleName)
                    .getSingleResult();

            return Optional.of(role);

        } catch (NoResultException e) {
            logger.error("Роль {} не найдена", roleName);
            return Optional.empty();
        }
    }
}

