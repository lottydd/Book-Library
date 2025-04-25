package com.example.booklibrary.dao;

import java.util.List;

public interface GenericDAO<T, ID> {
    T findById(ID id);

    List<T> findAll();

    void save(T entity);

    void update(T entity);

    void delete(int id);
}
