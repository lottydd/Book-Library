package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCatalogDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dao.CatalogDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogTreeDTO;
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

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    @Autowired
    public CatalogService(CatalogDAO catalogDAO, BookDAO bookDAO, BookCatalogDAO bookCatalogDAO, CatalogMapper catalogMapper) {
        this.catalogDAO = catalogDAO;
        this.bookDAO = bookDAO;
        this.bookCatalogDAO = bookCatalogDAO;
        this.catalogMapper = catalogMapper;
    }

    @Transactional
    public CatalogCreateResponseDTO createCatalog(CatalogCreateDTO dto) {
        logger.debug("Попытка создания каталога");
        Catalog catalog = new Catalog();
        catalog.setName(dto.getName());

        if (dto.getParentId() != null) {
            Catalog parent = catalogDAO.findById(dto.getParentId())
                    .orElseThrow(() -> {
                        logger.warn("Родительский каталог не найден по ID: {}", dto.getParentId());
                        return new EntityNotFoundException("Parent catalog not found");
                    });
            catalog.setParent(parent);

            catalogDAO.save(catalog); // Явное сохранение
            parent.getChildren().add(catalog); //  Обновляем в памяти для согласованности, можно и не делать
        } else {
            catalogDAO.save(catalog); // Для корневых
        }

        catalogDAO.flush(); // Синхронизация чтобы получить айпи
        logger.info("Каталог успешно создан. ID: {}", catalog.getId());

        return catalogMapper.toCatalogCreateResponseDTO(catalog);
    }
    @Transactional
    public CatalogAddBookResponseDTO addBookToCatalog(CatalogAddBookDTO dto) {
        logger.debug("Добавление книги ID {} в каталог ID {}", dto.getBookId(), dto.getCatalogId());
        Book book = bookDAO.findById(dto.getBookId())
                .orElseThrow(() -> {
                    logger.warn("Книга не найдена по ID: {}", dto.getBookId());
                    return new EntityNotFoundException("Книга не найдена");
                });
        Catalog catalog = catalogDAO.findById(dto.getCatalogId())
                .orElseThrow(() -> {
                    logger.warn("Каталог не найден по ID: {}", dto.getCatalogId());
                    return new EntityNotFoundException("Каталог не найден");
                });
        if (bookCatalogDAO.existsByBookAndCatalog(book, catalog)) {
            logger.warn("Книга уже находится в каталоге. Book ID: {}, Catalog ID: {}", dto.getBookId(), dto.getCatalogId());
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
    public void deleteCatalog(RequestIdDTO dto) {
        logger.debug("Попытка удаления каталога ID {}", dto.getId());
        Catalog catalog = catalogDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.warn("Каталог не найден по ID: {}", dto.getId());
                    return new EntityNotFoundException("Catalog not found");
                });
        deleteCatalogRecursive(catalog);
        logger.info("Каталог ID {} успешно удален", dto.getId());
    }

    @Transactional(readOnly = true)
    public List<CatalogTreeDTO> getCatalogTree() {
        logger.debug("Получение дерева каталогов");
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
        logger.debug("Рекурсивное удаление каталога ID {}", catalog.getId());
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
