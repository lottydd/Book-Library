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

    public List<BookCatalog> findByBookId(int bookId) {
        return entityManager.createQuery(
                        "SELECT bc FROM BookCatalog bc WHERE bc.book.id = :bookId",
                        BookCatalog.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    public void deleteAll(List<BookCatalog> bookCatalogs) {
        entityManager.createQuery(
                        "DELETE FROM BookCatalog bc WHERE bc IN :bookCatalogs")
                .setParameter("bookCatalogs", bookCatalogs)
                .executeUpdate();
    }
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
