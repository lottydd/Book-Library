package com.example.booklibrary.dao;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {
    Optional<T> findById(ID id);

    List<T> findAll();

    T save(T entity);

    T update(T entity);

    void delete(ID id);

    void saveAll(List<T> entities);

    void flush();
}
