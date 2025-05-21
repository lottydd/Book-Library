package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCatalogDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dao.CatalogDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogBooksResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogTreeDTO;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.mapper.CatalogMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final BookMapper bookMapper;

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    @Autowired
    public CatalogService(CatalogDAO catalogDAO, BookDAO bookDAO, BookCatalogDAO bookCatalogDAO, CatalogMapper catalogMapper, BookMapper bookMapper) {
        this.catalogDAO = catalogDAO;
        this.bookDAO = bookDAO;
        this.bookCatalogDAO = bookCatalogDAO;
        this.catalogMapper = catalogMapper;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public CatalogCreateResponseDTO createCatalog(CatalogCreateDTO dto) {
        logger.info("Попытка создания каталога");
        Catalog catalog = new Catalog();
        catalog.setName(dto.getName());

        if (dto.getParentId() != null) {
            Catalog parent = catalogDAO.findById(dto.getParentId())
                    .orElseThrow(() -> {
                        logger.error("Родительский каталог не найден по ID: {}", dto.getParentId());
                        return new EntityNotFoundException("Parent catalog not found");
                    });
            catalog.setParent(parent);

            catalogDAO.save(catalog);
            parent.getChildren().add(catalog);
        } else {
            catalogDAO.save(catalog);
        }

        catalogDAO.flush();
        logger.info("Каталог успешно создан. ID: {}", catalog.getId());

        return catalogMapper.toCatalogCreateResponseDTO(catalog);
    }

    @Transactional
    public CatalogAddBookResponseDTO addBookToCatalog(CatalogAddBookDTO dto) {
        logger.info("Добавление книги ID {} в каталог ID {}", dto.getBookId(), dto.getCatalogId());
        Book book = bookDAO.findById(dto.getBookId())
                .orElseThrow(() -> {
                    logger.error("Книга не найдена по ID: {}", dto.getBookId());
                    return new EntityNotFoundException("Книга не найдена");
                });
        Catalog catalog = catalogDAO.findById(dto.getCatalogId())
                .orElseThrow(() -> {
                    logger.error("Каталог не найден по ID: {}", dto.getCatalogId());
                    return new EntityNotFoundException("Каталог не найден");
                });
        if (bookCatalogDAO.existsByBookAndCatalog(book, catalog)) {
            logger.error("Книга уже находится в каталоге. Book ID: {}, Catalog ID: {}", dto.getBookId(), dto.getCatalogId());
            throw new IllegalStateException("Книга уже находится в каталоге");
        }

        BookCatalog bookCatalog = new BookCatalog();
        bookCatalog.setBook(book);
        bookCatalog.setCatalog(catalog);

        BookCatalog savedBookCatalog = bookCatalogDAO.save(bookCatalog);
        logger.info("Книга успешно добавлена в каталог. Book ID: {}, Catalog ID: {}", dto.getBookId(), dto.getCatalogId());
        return catalogMapper.toCatalogAddBookResponseDTO(savedBookCatalog);
    }

    @Transactional
    public void removeBookFromCatalog(int catalogId, int bookId) {
        logger.info("Удаление книги ID {} из каталога ID {}", bookId, catalogId);

        boolean exists = bookCatalogDAO.existsByCatalogIdAndBookId(catalogId, bookId);
        if (!exists) {
            logger.error("Связь книга ID {} — каталог ID {} не найдена", bookId, catalogId);
            throw new EntityNotFoundException("Книга не найдена в указанном каталоге");
        }

        bookCatalogDAO.deleteByCatalogIdAndBookId(catalogId, bookId);
        logger.info("Книга ID {} успешно удалена из каталога ID {}", bookId, catalogId);
    }

    @Transactional
    public void removeBookFromAllCatalogs(int bookId) {
        logger.info("Удаление книги ID {} из всех каталогов", bookId);

        if (!bookDAO.existsById(bookId)) {
            logger.error("Книга с ID  {} не найдена", bookId);
            throw new EntityNotFoundException("Book not found");
        }
        int deletedCount = bookCatalogDAO.deleteByBookId(bookId);
        logger.info("Удалено {} связей книги с каталогами", deletedCount);
    }

    @Transactional
    public void deleteCatalog(RequestIdDTO dto) {
        logger.info("Попытка удаления каталога ID {}", dto.getId());
        Catalog catalog = catalogDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.error("Каталог не найден по ID: {}", dto.getId());
                    return new EntityNotFoundException("Catalog not found");
                });
        deleteCatalogRecursive(catalog);
        logger.info("Каталог ID {} успешно удален", dto.getId());
    }

    @Transactional(readOnly = true)
    public List<CatalogTreeDTO> getCatalogTree() {
        logger.info("Получение дерева каталогов");
        List<Catalog> rootCatalogs = catalogDAO.findRootCatalogs();
        List<Catalog> fullTree = fetchChildrenRecursively(rootCatalogs);
        return catalogMapper.toTreeDtoList(fullTree);
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
        logger.info("Рекурсивное удаление каталога ID {}", catalog.getId());

        if (!catalog.getChildren().isEmpty()) {
            List<Catalog> children = new ArrayList<>(catalog.getChildren());
            for (Catalog child : children) {
                deleteCatalogRecursive(child);
            }
        }
        Catalog parent = catalog.getParent();
        if (parent != null) {
            parent.getChildren().remove(catalog);
            catalogDAO.update(parent);
        }

        catalogDAO.delete(catalog.getId());
        logger.info("Каталог ID  {} удален", catalog.getId());
    }

    @Transactional(readOnly = true)
    public List<CatalogBooksResponseDTO> getCatalogBooks(int catalogId) {
        List<BookCatalog> bookCatalogs = bookCatalogDAO.findByCatalogId(catalogId);

        return bookCatalogs.stream()
                .map(bc -> {
                    Book book = bc.getBook();
                    return bookMapper.toCatalogBookResponseDTO(book);
                })
                .toList();
    }
}
