package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import jakarta.persistence.EntityManager;

public class BookDAO extends BaseDAO<Book, Integer>{

    public BookDAO() {
        super(Book.class);
    }
}
