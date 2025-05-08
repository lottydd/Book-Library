package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCatalogDAO;
import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.bookcopy.BookCopyDTO;
import com.example.booklibrary.mapper.BookCopyMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service

public class BookCopyService {

    private final BookCopyDAO bookCopyDAO;
    private final BookCopyMapper bookCopyMapper;
    private final BookDAO bookDAO;
    private final CatalogService catalogService;

    private static final Logger logger = LoggerFactory.getLogger(BookCopyService.class);


    public BookCopyService(BookCopyDAO bookCopyDAO, BookCopyMapper bookCopyMapper, BookDAO bookDAO, CatalogService catalogService) {
        this.bookCopyDAO = bookCopyDAO;
        this.bookCopyMapper = bookCopyMapper;
        this.bookDAO = bookDAO;
        this.catalogService = catalogService;
    }

    @Transactional
    public void addCopies(int bookId, int count) {
        logger.debug("Попытка добавления {} копий для книги {}", count, bookId);
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        logger.debug("Книга найдена: ID: {}, Название: '{}'",
                book.getId(), book.getBookTitle());
        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            copies.add(BookCopy.builder()
                    .book(book)
                    .status(CopyStatus.AVAILABLE)
                    .build());
        }
        logger.debug("Создано {} новых копий. Статус: {}",
                copies.size(), CopyStatus.AVAILABLE);

        bookCopyDAO.saveAll(copies);
        logger.info("Успешно добавлено {} копий для книги ID: {}",
                count,
                bookId);
    }

    @Transactional
    public BookCopyDTO updateCopyStatus(int copyId, CopyStatus status) {
        logger.debug("Попытка обновления статуса {} копии  {}", status, copyId);
        BookCopy copy = getCopyById(copyId);
        validateStatusChange(copy, status);
        copy.setStatus(status);
        logger.debug("Cтатус копии обновлен: {}", status);
        return bookCopyMapper.toDto(bookCopyDAO.save(copy));
    }

    @Transactional
    public void deleteAllCopiesForBook(int bookId) {
        bookCopyDAO.deleteByBookId(bookId);
    }

    @Transactional
    public boolean hasRentedCopies(int bookId) {
        return bookCopyDAO.existsByBookIdAndStatus(bookId, CopyStatus.RENTED);
    }

    private BookCopy getCopyById(int copyId) {
        return bookCopyDAO.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Копия не найдена"));
    }

    @Transactional
    public void deleteDependencies(Book book) {
        catalogService.removeBookFromAllCatalogs(book.getId());
        book.getCopies().forEach(copy ->
                bookCopyDAO.delete(copy.getCopyId()));
    }

    private void validateStatusChange(BookCopy copy, CopyStatus newStatus) {
        if (newStatus == CopyStatus.RENTED && copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException("Копия должна быть доступна для аренды");
        }
    }


}
