package com.example.booklibrary.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public abstract class BaseDAO<T, ID> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;

    private static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    @PersistenceContext
    protected EntityManager entityManager;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<T> findById(ID id) {
        logger.debug("Попытка поиска сущности {} с ID: {}", entityClass.getSimpleName(), id);
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            logger.info("Сущность {} с ID {} не найдена", entityClass.getSimpleName(), id);
        } else {
            logger.debug("Сущность {} с ID {} успешно найдена", entityClass.getSimpleName(), id);
        }
        return Optional.ofNullable(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public List<T> findAll() {
        logger.debug("Попытка получения всех записей сущности {}", entityClass.getSimpleName());
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        List<T> result = entityManager.createQuery(jpql, entityClass).getResultList();
        logger.info("Найдено {} записей сущности {}", result.size(), entityClass.getSimpleName());
        return result;
    }

    @Transactional
    @Override
    public T save(T entity) {
        logger.debug("Сохранение сущности {}", entityClass.getSimpleName());
        entityManager.persist(entity);
        logger.info("Сущность {} сохранена", entityClass.getSimpleName());
        return entity;
    }

    @Transactional
    @Override
    public T update(T entity) {
        logger.debug("Обновление сущности {}", entityClass.getSimpleName());
        T updatedEntity = entityManager.merge(entity);
        logger.info("Сущность {} обновлена", entityClass.getSimpleName());
        return updatedEntity;
    }

    @Transactional
    @Override
    public void delete(ID id) {
        logger.debug("Попытка удаление сущности {} ", entityClass.getSimpleName());
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
            logger.info("Сущность {} удалена", entityClass.getSimpleName());
        } else {
            logger.warn("Сущность {} для удаления не найдена", entityClass.getSimpleName());
        }
    }


    @Transactional
    @Override
    public void saveAll(List<T> entities) {
        logger.debug("Пакетное сохранение {} сущностей {}", entities.size(), entityClass.getSimpleName());
        entities.forEach(entityManager::persist);
        logger.info("Сохранено {} сущностей {}", entities.size(), entityClass.getSimpleName());
    }
}