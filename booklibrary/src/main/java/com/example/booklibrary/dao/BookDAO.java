package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class BookDAO extends BaseDAO<Book, Integer> {

    public BookDAO() {
        super(Book.class);
    }

    public Optional<Book> findByIsbn(String isbn) {
        try {
            return Optional.ofNullable(entityManager.createQuery(
                            "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                    .setParameter("isbn", isbn)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Book> search(String query) {
        String searchPattern = "%" + query.toLowerCase() + "%";
        return entityManager.createQuery(
                        "SELECT b FROM Book b WHERE " +
                                "LOWER(b.author) LIKE :pattern OR " +
                                "LOWER(b.bookTitle) LIKE :pattern OR " +
                                "b.isbn LIKE :pattern", Book.class)
                .setParameter("pattern", searchPattern)
                .getResultList();
    }

}

