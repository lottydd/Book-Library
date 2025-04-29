package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository

public class BookDAO extends BaseDAO<Book, Integer>{

    public BookDAO() {
        super(Book.class);
    }
}
