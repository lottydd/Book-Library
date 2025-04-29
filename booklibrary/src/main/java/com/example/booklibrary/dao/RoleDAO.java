package com.example.booklibrary.dao;


import com.example.booklibrary.model.Role;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class RoleDAO extends BaseDAO<Role, Integer> {

    public RoleDAO() {
        super(Role.class);
    }

    public Optional<Role> findRoleName(String roleName) {
        try {
            Role role = entityManager.createQuery(
                            "SELECT r FROM Role r WHERE r.roleName = :roleName",
                            Role.class
                    )
                    .setParameter("roleName", roleName)
                    .getSingleResult();

            return Optional.of(role);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}

