package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class BookCatalogDAO extends BaseDAO<BookCatalog, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookCatalogDAO.class);

    public BookCatalogDAO() {
        super(BookCatalog.class);
    }

    public boolean existsByBookAndCatalog(Book book, Catalog catalog) {
        logger.info("Проверка существования {} в Каталоге {}"
                , book.getBookTitle(), catalog.getName());
        Long count = entityManager.createQuery(
                        "SELECT COUNT(bc) FROM BookCatalog bc " +
                                "WHERE bc.book = :book AND bc.catalog = :catalog",
                        Long.class
                )
                .setParameter("book", book)
                .setParameter("catalog", catalog)
                .getSingleResult();
        if (count == 0) {
            logger.info("Книга {} не существует в Каталоге {}", book.getBookTitle(), catalog.getName());
        } else {
            logger.info("Книга {}  существует в Каталоге {}", book.getBookTitle(), catalog.getName());

        }
        return count > 0;
    }

    public boolean existsByCatalogIdAndBookId(int catalogId, int bookId) {
        logger.info("Проверка существования книги в каталоге");
        String ql = "SELECT count(bc) FROM BookCatalog bc WHERE bc.catalog.id = :catalogId AND bc.book.id = :bookId";
        Long count = entityManager.createQuery(ql, Long.class)
                .setParameter("catalogId", catalogId)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return count > 0;
    }

    public void deleteByCatalogIdAndBookId(int catalogId, int bookId) {
        logger.info("Удаление записей связей книга-каталог");

        String ql = "DELETE FROM BookCatalog bc WHERE bc.catalog.id = :catalogId AND bc.book.id = :bookId";
        int deleted = entityManager.createQuery(ql)
                .setParameter("catalogId", catalogId)
                .setParameter("bookId", bookId)
                .executeUpdate();
        logger.info("Удалено {} записей связи книга-каталог", deleted);
    }

    public int deleteByBookId(int bookId) {
        logger.info("Массовое удаление связей книги ID {}", bookId);
        int deletedCount = entityManager.createQuery(
                        "DELETE FROM BookCatalog bc WHERE bc.book.id = :bookId")
                .setParameter("bookId", bookId)
                .executeUpdate();

        logger.info("Удалено {} связей для книги ID {}", deletedCount, bookId);
        return deletedCount;
    }

    public List<BookCatalog> findByCatalogId(int catalogId) {
        logger.info("Поиск книг по ID каталога {}", catalogId);
        return entityManager.createQuery(
                        "SELECT bc FROM BookCatalog bc " +
                                "JOIN FETCH bc.book " +
                                "WHERE bc.catalog.id = :catalogId", BookCatalog.class)
                .setParameter("catalogId", catalogId)
                .getResultList();
    }
}

