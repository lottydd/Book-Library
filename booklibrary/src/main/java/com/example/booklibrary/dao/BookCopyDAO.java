package com.example.booklibrary.dao;

import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class BookCopyDAO extends BaseDAO<BookCopy, Integer> {
    public BookCopyDAO() {
        super(BookCopy.class);
    }

    private static final Logger logger = LoggerFactory.getLogger(BookCopyDAO.class);


    public void deleteByBookId(int bookId) {
        logger.debug("Удаление всех копий книги с ID {}", bookId);
        int deletedCount = entityManager.createQuery("DELETE FROM BookCopy c WHERE c.book.id = :bookId")
                .setParameter("bookId", bookId)
                .executeUpdate();
        logger.info("Удалено {} копий книги с ID {}", deletedCount, bookId);
    }

    public boolean existsByBookIdAndStatus(int bookId, CopyStatus status) {
        logger.debug("Поиск книги с {} и статусом {}", bookId, status);
       boolean exists= entityManager.createQuery(
                        "SELECT COUNT(c) > 0 FROM BookCopy c " +
                                "WHERE c.book.id = :bookId AND c.status = :status", Boolean.class)
                .setParameter("bookId", bookId)
                .setParameter("status", status)
                .getSingleResult();
        if (exists) {
            logger.info("Найдены копии книги ID {} со статусом {}", bookId, status);
        } else {
            logger.debug("Не найдено копий книги ID {} со статусом {}", bookId, status);
        }
        return exists;
    }


    public List<BookCopy> findAllByBookId(int bookId) {
        return entityManager.createQuery(
                        "SELECT c FROM BookCopy c WHERE c.book.id = :bookId", BookCopy.class)
                .setParameter("bookId", bookId)
                .getResultList();
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
