package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.book.*;
import com.example.booklibrary.dto.response.book.BookDetailsDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookService {

    private BookDAO bookDAO;
    private final CatalogService catalogService;
    private final BookMapper bookMapper;
    private final BookCopyService bookCopyService;

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public BookService(CatalogService catalogService, BookMapper bookMapper, BookCopyService bookCopyService) {
        this.catalogService = catalogService;
        this.bookMapper = bookMapper;
        this.bookCopyService = bookCopyService;
    }

    @Transactional
    public BookResponseDTO createBook(BookCreateDTO dto) {
        logger.debug("Попытка создания книги");
        if (bookDAO.findByIsbn(dto.getIsbn()).isPresent()) {
            logger.warn("Книга с таким ISBN уже существует");
            throw new IllegalStateException("Книга с таким ISBN уже существует");
        }
        Book book = bookMapper.toEntity(dto);
        book.setStorageArrivalDate(LocalDateTime.now());
        Book savedBook = bookDAO.save(book);

        logger.debug("Добавление {} копий книги", dto.getCopiesCount());
        bookCopyService.addCopiesByCountAndId(savedBook.getId(), dto.getCopiesCount());
        logger.info("Книга успешно создана. ID: {}", savedBook.getId());
        return bookMapper.toResponseDTO(savedBook);
    }

    @Transactional
    public BookResponseDTO updateBook(BookUpdateDTO dto) {
        logger.debug("Обновление книги по ISBN: {}", dto.getIsbn());
        Book book = bookDAO.findByIsbn(dto.getIsbn())
                .orElseThrow(() -> {
                    logger.warn("Книга не найдена по ISBN: {}", dto.getIsbn());
                    return new EntityNotFoundException("Книга не найдена");
                });

        book.setAuthor(dto.getAuthor());
        book.setBookTitle(dto.getBookTitle());
        book.setPublicationYear(dto.getPublicationYear());
        book.setDescription(dto.getDescription());

        Book updatedBook = bookDAO.save(book);
        logger.info("Данные книги обновлены. ID: {}, ISBN: {}", updatedBook.getId(), updatedBook.getIsbn());

        return bookMapper.toResponseDTO(updatedBook);
    }

    @Transactional
    public void deleteBook(RequestIdDTO dto) {
        logger.debug("Попытка удаления Книги {}", dto.getId());
        Book book = findBookByIdOrThrow(dto.getId());
        validateDeletion(book);
        logger.debug("Удаление зависимостей Книги {}", dto.getId());
        catalogService.removeBookFromAllCatalogs(dto.getId());
        bookCopyService.deleteBookCopies(dto.getId());
        bookDAO.delete(dto.getId());
        logger.info("Книга {} успешно удалена ", dto.getId());
    }

    @Transactional(readOnly = true)
    public BookDetailsDTO getBookDetails(RequestIdDTO dto) {
        logger.debug("Попытка получения информации о Книге через ID: {}", dto.getId());

        Book book = bookDAO.findByIdWithCopies(dto.getId())
                .orElseThrow(() -> {
                    logger.warn("Книга не найдена по ID: {}", dto.getId());
                    return new EntityNotFoundException("Книга не найдена");
                });
        return bookMapper.toDetailsDTO(book);
    }

    private Book findBookByIdOrThrow(int bookId) {
        return bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
    }

    private void validateDeletion(Book book) {
        logger.debug("Валидация удаления книги");
        if (book.getCopies().stream().anyMatch(c -> c.getStatus() == CopyStatus.RENTED)) {
            logger.info("Невозможно удалить книгу, т.к. у книги существуют активные Аренды");
            throw new IllegalStateException("Невозможно удалить книгу, т.к. у книги существуют активные Аренды");
        }
    }
}