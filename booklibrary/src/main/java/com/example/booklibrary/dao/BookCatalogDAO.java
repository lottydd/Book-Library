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

    public List<BookCatalog> findByBookId(int bookId) {
        logger.debug("Поиск списка каталогов в которых содержится книга с ID {}", bookId);
        List<BookCatalog> resultList = entityManager.createQuery(
                        "SELECT bc FROM BookCatalog bc WHERE bc.book.id = :bookId",
                        BookCatalog.class)
                .setParameter("bookId", bookId)
                .getResultList();
        logger.info("Найдено {} каталогов в которых содержится книга с ID {}", resultList.size(), bookId);
        return resultList;
    }

    public void deleteAll(List<BookCatalog> bookCatalogs) {
        logger.debug("Удаление всех записей из списка");
        entityManager.createQuery(
                        "DELETE FROM BookCatalog bc WHERE bc IN :bookCatalogs")
                .setParameter("bookCatalogs", bookCatalogs)
                .executeUpdate();
        logger.info("Удалено  {} записей", bookCatalogs.size());
    }

    public boolean existsByBookAndCatalog(Book book, Catalog catalog) {
        logger.debug("Проверка существования {} в Каталоге {}"
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
            logger.debug("Книга {} не существует в Каталоге {}", book.getBookTitle(), catalog.getName());
        }
        else {
            logger.info("Книга {}  существует в Каталоге {}", book.getBookTitle(), catalog.getName());

        }
        return count > 0;
    }

    public List<Integer> findCatalogIdsByBookId(int bookId) {
        logger.debug("Получение ID каталогов для книги {}", bookId);
        return entityManager.createQuery(
                        "SELECT bc.catalog.id FROM BookCatalog bc WHERE bc.book.id = :bookId",
                        Integer.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    public void deleteNotInCatalogs(int bookId, List<Integer> catalogIds) {
        logger.debug("Удаление старых каталогов книги {}", bookId);
        entityManager.createQuery(
                        "DELETE FROM BookCatalog bc WHERE bc.book.id = :bookId AND bc.catalog.id NOT IN :catalogIds")
                .setParameter("bookId", bookId)
                .setParameter("catalogIds", catalogIds)
                .executeUpdate();
    }

    public void addToCatalogs(int bookId, List<Integer> catalogIds) {
        logger.debug("Добавление книги {} в {} каталогов", bookId, catalogIds.size());

        Book book = entityManager.find(Book.class, bookId);
        if (book == null) {
            throw new EntityNotFoundException("Book not found with id: " + bookId);
        }

        for (Integer catalogId : catalogIds) {
            Catalog catalog = entityManager.find(Catalog.class, catalogId);
            if (catalog == null) {
                throw new EntityNotFoundException("Catalog not found with id: " + catalogId);
            }

            BookCatalog bc = new BookCatalog();
            bc.setBook(book);
            bc.setCatalog(catalog);
            entityManager.persist(bc);
        }
    }

    public boolean existsByCatalogIdAndBookId(int catalogId, int bookId) {
        String ql = "SELECT count(bc) FROM BookCatalog bc WHERE bc.catalog.id = :catalogId AND bc.book.id = :bookId";
        Long count = entityManager.createQuery(ql, Long.class)
                .setParameter("catalogId", catalogId)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return count > 0;
    }

    public void deleteByCatalogIdAndBookId(int catalogId, int bookId) {
        String ql = "DELETE FROM BookCatalog bc WHERE bc.catalog.id = :catalogId AND bc.book.id = :bookId";
        int deleted = entityManager.createQuery(ql)
                .setParameter("catalogId", catalogId)
                .setParameter("bookId", bookId)
                .executeUpdate();
        logger.debug("Удалено {} записей связи книга-каталог", deleted);
    }


    public int deleteByBookId(int bookId) {
        logger.debug("Массовое удаление связей книги ID {}", bookId);
        int deletedCount = entityManager.createQuery(
                        "DELETE FROM BookCatalog bc WHERE bc.book.id = :bookId")
                .setParameter("bookId", bookId)
                .executeUpdate();

        logger.info("Удалено {} связей для книги ID {}", deletedCount, bookId);
        return deletedCount;
    }

    public List<BookCatalog> findByCatalogId(int catalogId) {
        logger.debug("Поиск книг по ID каталога {}", catalogId);
        return entityManager.createQuery(
                        "SELECT bc FROM BookCatalog bc " +
                                "JOIN FETCH bc.book " +
                                "WHERE bc.catalog.id = :catalogId", BookCatalog.class)
                .setParameter("catalogId", catalogId)
                .getResultList();
    }

}

