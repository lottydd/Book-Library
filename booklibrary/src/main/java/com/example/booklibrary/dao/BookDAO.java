package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class BookDAO extends BaseDAO<Book, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BookDAO.class);

    public BookDAO() {
        super(Book.class);
    }

    public Optional<Book> findByIdWithCopies(int bookId) {
        logger.info("Поиск книги вместе с ее копиями по ID {}", bookId);
        try {
            Book book = entityManager.createQuery(
                            "SELECT b FROM Book b LEFT JOIN FETCH b.copies WHERE b.id = :id",
                            Book.class)
                    .setParameter("id", bookId)
                    .getSingleResult();
            if (book.getCopies() != null) {
                logger.info("Найдена книга ID: {} с {} копиями", bookId, book.getCopies().size());
            } else {
                logger.info("Найдена книга ID: {} (копии не загружены)", bookId);
            }
            return Optional.of(book);

        } catch (NoResultException e) {
            logger.error("Книга с ID: {} не найдена", bookId);
            return Optional.empty();
        }
    }


    public Optional<Book> findByIsbn(String isbn) {
        logger.info("Поиск книги по ее isbn {}", isbn);
        try {
            Book book = entityManager.createQuery(
                            "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                    .setParameter("isbn", isbn)
                    .getSingleResult();
            logger.info("Книга с isbn {} найдена", isbn);
            return Optional.of(book);
        } catch (NoResultException e) {
            logger.error("Книга с isbn {} не найдена", isbn);
            return Optional.empty();
        }
    }

    public boolean existsById(int bookId) {
        logger.info("Проверка существования Книги по ID {}", bookId);

        Boolean exists = entityManager.createQuery(
                        "SELECT COUNT(b) > 0 FROM Book b WHERE b.id = :bookId", Boolean.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
        if (exists) {
            logger.info("Книга с  ID {}  существует", bookId);
        } else {
            logger.info("Книга с  ID {} не  существует", bookId);

        }
        return exists;
    }


}

