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
            logger.info("Не найдено копий книги ID {} со статусом {}", bookId, status);
        }
        return exists;
    }

    public List<BookCopy> findRentedCopiesByBookId(int bookId) {
        logger.debug("Поиск арендованных копий книги ID {}", bookId);

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

}
