package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCatalogDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dao.CatalogDAO;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {
    private CatalogDAO catalogDAO;
    private BookDAO bookDAO;
    private BookCatalogDAO bookCatalogDAO;

    public CatalogService(CatalogDAO catalogDAO, BookDAO bookDAO, BookCatalogDAO bookCatalogDAO) {
        this.catalogDAO = catalogDAO;
        this.bookDAO = bookDAO;
        this.bookCatalogDAO = bookCatalogDAO;
    }

    @Transactional
    public void addBookToCatalogs(int bookId, List<Integer> catalogIds) {
        if (!bookDAO.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found");
        }
        catalogIds.forEach(catalogId -> addBookToCatalog(bookId, catalogId));
    }

    @Transactional

    public void updateCatalogs(int bookId, List<Integer> catalogIds) {
        removeBookFromAllCatalogs(bookId);
        addBookToCatalogs(bookId, catalogIds);
    }

    @Transactional

    public void removeBookFromAllCatalogs(int bookId) {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        List<BookCatalog> bookCatalogs = bookCatalogDAO.findByBookId(bookId);
        bookCatalogDAO.deleteAll(bookCatalogs);
    }

    @Transactional

    public void createCatalog(String name, Integer parentId) {
        Catalog catalog = new Catalog();
        catalog.setName(name);
        if (parentId != null) {
            Catalog parent = catalogDAO.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent catalog not found"));
            catalog.setParent(parent);
            parent.getChildren().add(catalog);
            catalogDAO.update(parent);
        }
        catalogDAO.save(catalog);


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


    @Transactional
    public void addBookToCatalog(int bookId, int catalogId) {
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

}
