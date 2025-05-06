package com.example.booklibrary.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public abstract class BaseDAO<T, ID> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return entityManager.createQuery(jpql, entityClass).getResultList();
    }

    @Transactional
    @Override
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    @Override
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    @Override
    public void delete(ID id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }



}