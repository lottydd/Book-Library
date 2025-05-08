package com.example.booklibrary.dao;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
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
}
