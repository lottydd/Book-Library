package com.example.booklibrary.dao;

import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import jakarta.validation.constraints.NotNull;
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
        logger.info("Удаление всех копий книги с ID {}", bookId);
        int deletedCount = entityManager.createQuery("DELETE FROM BookCopy c WHERE c.book.id = :bookId")
                .setParameter("bookId", bookId)
                .executeUpdate();
        logger.info("Удалено {} копий книги с ID {}", deletedCount, bookId);
    }

    public List<BookCopy> findRentedCopiesByBookId(int bookId) {
        logger.info("Поиск арендованных копий книги ID {}", bookId);

        List<BookCopy> result = entityManager.createQuery(
                        "SELECT c FROM BookCopy c WHERE c.book.id = :bookId AND c.status = :status", BookCopy.class)
                .setParameter("bookId", bookId)
                .setParameter("status", CopyStatus.RENTED)
                .getResultList();

        if (!result.isEmpty()) {
            logger.info("Найдены {} арендованных копий книги ID {}", result.size(), bookId);
        } else {
            logger.info("Арендованные копии книги ID {} не найдены", bookId);
        }

        return result;
    }

    public List<BookCopy> findNonDeletableCopiesByBookId(int bookId) {
        return entityManager.createQuery(
                        "SELECT c FROM BookCopy c WHERE c.book.id = :bookId AND c.status <> :availableStatus", BookCopy.class)
                .setParameter("bookId", bookId)
                .setParameter("availableStatus", CopyStatus.AVAILABLE)
                .getResultList();
    }
}
