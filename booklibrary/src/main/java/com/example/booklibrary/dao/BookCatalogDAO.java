package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class BookCatalogDAO extends BaseDAO<BookCatalog, Integer> {
    public BookCatalogDAO() {
        super(BookCatalog.class);    }


    public void deleteAll(List<BookCatalog> bookCatalogs) {
        bookCatalogs.forEach(bc -> entityManager.remove(bc));
    }

    // Проверить, существует ли связь
    public boolean existsByBookAndCatalog(Book book, Catalog catalog) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(bc) FROM BookCatalog bc " +
                                "WHERE bc.book = :book AND bc.catalog = :catalog",
                        Long.class
                )
                .setParameter("book", book)
                .setParameter("catalog", catalog)
                .getSingleResult();

        return count > 0;
    }
}
