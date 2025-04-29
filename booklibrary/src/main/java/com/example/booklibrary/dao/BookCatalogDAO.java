package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class BookCatalogDAO extends BaseDAO<BookCatalog, Integer> {
    public BookCatalogDAO() {
        super(BookCatalog.class);    }


    public void deleteAll(List<BookCatalog> bookCatalogs) {
        bookCatalogs.forEach(bc -> entityManager.remove(bc));
    }
}
