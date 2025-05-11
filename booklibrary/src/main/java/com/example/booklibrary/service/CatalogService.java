package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCatalogDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dao.CatalogDAO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.mapper.CatalogMapper;
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

@Service
public class CatalogService {
    private final CatalogDAO catalogDAO;
    private final BookDAO bookDAO;
    private final BookCatalogDAO bookCatalogDAO;
    private final CatalogMapper catalogMapper;

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    public CatalogService(CatalogDAO catalogDAO, BookDAO bookDAO, BookCatalogDAO bookCatalogDAO, CatalogMapper catalogMapper) {
        this.catalogDAO = catalogDAO;
        this.bookDAO = bookDAO;
        this.bookCatalogDAO = bookCatalogDAO;
        this.catalogMapper = catalogMapper;
    }

    @Transactional
    public CatalogCreateResponseDTO createCatalog(CatalogCreateDTO dto) {
        Catalog catalog = new Catalog();
        catalog.setName(dto.getName());

        if (dto.getParentId() != null) {
            Catalog parent = catalogDAO.findById(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent catalog not found"));
            catalog.setParent(parent);
            parent.getChildren().add(catalog);
            catalogDAO.update(parent);
        }

        Catalog savedCatalog = catalogDAO.save(catalog);
        return catalogMapper.toCatalogCreateResponseDTO(savedCatalog);
    }

    @Transactional
    public CatalogAddBookResponseDTO addBookToCatalog(CatalogAddBookDTO dto) {
        Book book = bookDAO.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        Catalog catalog = catalogDAO.findById(dto.getCatalogId())
                .orElseThrow(() -> new EntityNotFoundException("Catalog not found"));

        if (bookCatalogDAO.existsByBookAndCatalog(book, catalog)) {
            throw new IllegalStateException("Book is already in the catalog");
        }

        BookCatalog bookCatalog = new BookCatalog();
        bookCatalog.setBook(book);
        bookCatalog.setCatalog(catalog);

        BookCatalog savedBookCatalog = bookCatalogDAO.save(bookCatalog);
        return catalogMapper.toCatalogAddBookResponseDTO(savedBookCatalog);
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

    @Transactional
    public void deleteCatalog(int catalogId) {
        Catalog catalog = catalogDAO.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException("Catalog not found"));
        deleteCatalogRecursive(catalog);
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
