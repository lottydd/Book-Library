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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookService {

    private final BookDAO bookDAO;
    private final CatalogService catalogService;
    private final BookMapper bookMapper;
    private final BookCopyService bookCopyService;

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    public BookService(BookDAO bookDAO, CatalogService catalogService, BookMapper bookMapper, BookCopyService bookCopyService) {
        this.bookDAO = bookDAO;
        this.catalogService = catalogService;
        this.bookMapper = bookMapper;
        this.bookCopyService = bookCopyService;
    }

    @Transactional
    public BookResponseDTO createBook(BookCreateDTO dto) {
        logger.info("Попытка создания книги");
        if (bookDAO.findByIsbn(dto.getIsbn()).isPresent()) {
            logger.error("Книга с таким ISBN уже существует");
            throw new IllegalStateException("Книга с таким ISBN уже существует");
        }
        Book book = bookMapper.toEntity(dto);
        book.setStorageArrivalDate(LocalDateTime.now());
        Book savedBook = bookDAO.save(book);

        logger.info("Добавление {} копий книги", dto.getCopiesCount());
        bookCopyService.addCopiesForNewBook(dto.getCopiesCount(), savedBook);
        logger.info("Книга успешно создана. ID: {}", savedBook.getId());
        return bookMapper.toResponseDTO(savedBook);
    }

    @Transactional
    public BookResponseDTO updateBook(BookUpdateDTO dto) {
        logger.info("Обновление книги по ISBN: {}", dto.getIsbn());
        Book book = bookDAO.findByIsbn(dto.getIsbn())
                .orElseThrow(() -> {
                    logger.error("Книга не найдена по ISBN: {}", dto.getIsbn());
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
        logger.info("Попытка удаления Книги {}", dto.getId());
        Book book = findBookByIdOrThrow(dto.getId());
        validateDeletion(book);
        logger.info("Удаление зависимостей Книги {}", dto.getId());
        catalogService.removeBookFromAllCatalogs(dto.getId());
        bookCopyService.deleteBookCopies(dto);
        bookDAO.delete(dto.getId());
        logger.info("Книга {} успешно удалена ", dto.getId());
    }

    @Transactional(readOnly = true)
    public BookDetailsDTO getBookDetails(RequestIdDTO dto) {
        logger.info("Попытка получения информации о Книге через ID: {}", dto.getId());

        Book book = bookDAO.findByIdWithCopies(dto.getId())
                .orElseThrow(() -> {
                    logger.error("Книга не найдена по ID: {}", dto.getId());
                    return new EntityNotFoundException("Книга не найдена");
                });
        return bookMapper.toDetailsDTO(book);
    }

    private Book findBookByIdOrThrow(int bookId) {
        return bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
    }

    private void validateDeletion(Book book) {
        logger.info("Валидация удаления книги");
        if (book.getCopies().stream().anyMatch(c -> c.getStatus() == CopyStatus.RENTED)) {
            logger.info("Невозможно удалить книгу, т.к. у книги существуют активные Аренды");
            throw new IllegalStateException("Невозможно удалить книгу, т.к. у книги существуют активные Аренды");
        }
    }
}