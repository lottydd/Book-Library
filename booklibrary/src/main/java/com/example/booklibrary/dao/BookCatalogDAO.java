package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;

public class BookCatalogDAO extends BaseDAO<BookCatalog, Integer> {
    public BookCatalogDAO() {
        super(BookCatalog.class);    }
}
