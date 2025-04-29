package com.example.booklibrary.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class BaseDAO<T, ID> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;

    }

    @Transactional(readOnly = true)
    @Override
    public Optional<T> findById(ID id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return entityManager.createQuery(jpql, entityClass).getResultList();
    }

    @Transactional
    public void save(T entity) {
        entityManager.persist(entity);
    }

    @Transactional
    @Override
    public void update(T entity) {
        entityManager.merge(entity);
    }

    @Transactional
    @Override
    public void delete(int id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
