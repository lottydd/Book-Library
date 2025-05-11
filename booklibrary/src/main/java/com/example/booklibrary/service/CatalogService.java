package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCatalogDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dao.CatalogDAO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    private final CatalogDAO catalogDAO;
    private final BookDAO bookDAO;
    private final BookCatalogDAO bookCatalogDAO;

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    public CatalogService(CatalogDAO catalogDAO, BookDAO bookDAO, BookCatalogDAO bookCatalogDAO) {
        this.catalogDAO = catalogDAO;
        this.bookDAO = bookDAO;
        this.bookCatalogDAO = bookCatalogDAO;
    }

//переделать на DtoResponse
    @Transactional
    public void createCatalog(CatalogCreateDTO dto) {
        Catalog catalog = new Catalog();
        catalog.setName(dto.getName());
        if (dto.getParentId() != null) {
            Catalog parent = catalogDAO.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent catalog not found"));
            catalog.setParent(parent);
            parent.getChildren().add(catalog);
            catalogDAO.update(parent);
        }
        catalogDAO.save(catalog);
    }

    //переделать под dto
    @Transactional
    public void addBookToCatalog(CatalogAddBookDTO dto , int bookId, int catalogId) {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        Catalog catalog = catalogDAO.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException("Catalog not found"));

        if (bookCatalogDAO.existsByBookAndCatalog(book, catalog)) {
            throw new IllegalStateException("Book is already in the catalog");
        }

        BookCatalog bookCatalog = new BookCatalog();
        bookCatalog.setBook(book);
        bookCatalog.setCatalog(catalog);
        bookCatalogDAO.save(bookCatalog);
    }


    @Transactional
    public void addBookToCatalogs(int bookId, List<Integer> catalogIds) {
        logger.debug("Попытка добавления книги {}  в {} каталог", bookId, catalogIds.size());
        if (!bookDAO.existsById(bookId)) {
            logger.warn("Книга с ID {} не найдена", bookId);
            throw new EntityNotFoundException("Книга не найдена");
        }
        catalogIds.forEach(catalogId -> addBookToCatalog(bookId, catalogId));
        logger.info("Книга {} добавлена в {} каталог", bookId, catalogIds.size());
    }

    //Остановка здесь)))
    @Transactional
    public void updateCatalogs(int bookId, List<Integer> newCatalogIds) {

        bookCatalogDAO.deleteNotInCatalogs(bookId, newCatalogIds);

        List<Integer> existingIds = bookCatalogDAO.findCatalogIdsByBookId(bookId);
        List<Integer> toAdd = newCatalogIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());

        if (!toAdd.isEmpty()) {
            bookCatalogDAO.addToCatalogs(bookId, toAdd);
        }
    }

    @Transactional

    public void removeBookFromAllCatalogs(int bookId) {
        logger.debug("Удаление книги ID {} из всех каталогов", bookId);

        if (!bookDAO.existsById(bookId)) {
            logger.warn("Книга с ID  {} не найдена", bookId);
            throw new EntityNotFoundException("Book not found");
        }
        int deletedCount = bookCatalogDAO.deleteByBookId(bookId);
        logger.info("Удалено {} связей книги с каталогами", deletedCount);
    }


    @Transactional(readOnly = true)
    public List<Catalog> getCatalogTree() {
        List<Catalog> rootCatalogs = catalogDAO.findRootCatalogs();
        return fetchChildrenRecursively(rootCatalogs);
    }

    @Transactional
    private List<Catalog> fetchChildrenRecursively(List<Catalog> catalogs) {
        catalogs.forEach(catalog -> {
            List<Catalog> children = catalogDAO.findByParentId(catalog.getId());
            catalog.setChildren(fetchChildrenRecursively(children));
        });
        return catalogs;
    }


    @Transactional

    public void deleteCatalog(int catalogId) {
        Catalog catalog = catalogDAO.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException("Catalog not found"));
        deleteCatalogRecursive(catalog);
    }

    @Transactional

    private void deleteCatalogRecursive(Catalog catalog) {
        if (!catalog.getBookCatalogs().isEmpty()) {
            bookCatalogDAO.deleteAll(catalog.getBookCatalogs());
        }
        if (!catalog.getChildren().isEmpty()) {
            List<Catalog> children = new ArrayList<>(catalog.getChildren());
            for (Catalog child : children) {
                deleteCatalogRecursive(child);
            }
            Catalog parent = catalog.getParent();
            if (parent != null) {
                parent.getChildren().remove(catalog);
                catalogDAO.update(parent);
            }
            catalogDAO.delete(catalog.getId());
        }
    }


}
