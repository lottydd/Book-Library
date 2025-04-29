package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import org.springframework.stereotype.Repository;

@Repository

public class BookCatalogDAO extends BaseDAO<BookCatalog, Integer> {
    public BookCatalogDAO() {
        super(BookCatalog.class);    }
}
