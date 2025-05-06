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
    public Optional<Book> findByIdWithCopies(int id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT b FROM Book b LEFT JOIN FETCH b.copies WHERE b.id = :id",
                            Book.class)
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
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

    public boolean existsById(int bookId) {
        return entityManager.createQuery(
                        "SELECT COUNT(b) > 0 FROM Book b WHERE b.id = :bookId", Boolean.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
    }



}

