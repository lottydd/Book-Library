package com.example.booklibrary.dao;

import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class BookCopyDAO extends BaseDAO< BookCopy, Integer> {
    public BookCopyDAO() {
        super(BookCopy.class);
    }

    public List<BookCopy> findAllByBookId(int bookId) {
        return entityManager.createQuery(
                        "SELECT c FROM BookCopy c WHERE c.book.id = :bookId", BookCopy.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    public boolean existsByBookIdAndStatus(int bookId, CopyStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) > 0 FROM BookCopy c " +
                                "WHERE c.book.id = :bookId AND c.status = :status", Boolean.class)
                .setParameter("bookId", bookId)
                .setParameter("status", status)
                .getSingleResult();
    }

    public long countByBookId(int bookId) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM BookCopy c WHERE c.book.id = :bookId", Long.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
    }

    public List<BookCopy> findByStatus(CopyStatus status) {
        return entityManager.createQuery(
                        "SELECT c FROM BookCopy c WHERE c.status = :status", BookCopy.class)
                .setParameter("status", status)
                .getResultList();
    }

}
