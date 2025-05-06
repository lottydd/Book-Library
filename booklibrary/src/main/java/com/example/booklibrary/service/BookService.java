package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.book.*;
import com.example.booklibrary.dto.request.bookcopy.BookCopyDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.mapper.BookCopyMapper;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BookService {

    @Autowired
    private BookDAO bookDAO;

    private final BookCopyDAO bookCopyDAO;
    private final CatalogService catalogService;
    private final BookMapper bookMapper;
    private final BookCopyMapper bookCopyMapper;
    private final BookCopyService bookCopyService;


    public BookService(BookCopyDAO bookCopyDAO, CatalogService catalogService, BookMapper bookMapper, BookCopyMapper bookCopyMapper, BookCopyService bookCopyService) {
        this.bookCopyDAO = bookCopyDAO;
        this.catalogService = catalogService;
        this.bookMapper = bookMapper;
        this.bookCopyMapper = bookCopyMapper;
        this.bookCopyService = bookCopyService;
    }

    @Transactional
    public BookResponseDTO addOrUpdateBook(BookAddDTO dto) {
        Book book = bookDAO.findByIsbn(dto.getIsbn())
                .orElseGet(() -> {
                    Book newBook = bookMapper.toEntity(dto);
                    newBook.setStorageArrivalDate(LocalDateTime.now());
                    return bookDAO.save(newBook);
                });

        bookCopyService.addCopies(book.getId(), dto.getCopiesCount());
        catalogService.updateCatalogs(book.getId(), dto.getCatalogIds());

        return bookMapper.toResponseDTO(book);
    }

    @Transactional
    public BookResponseDTO updateBook(int bookId, BookUpdateDTO dto) {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        bookMapper.updateFromDto(dto, book);
        return bookMapper.toResponseDTO(bookDAO.save(book));
    }

    @Transactional
    public void deleteBook(int bookId) {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        validateDeletion(book);
        deleteDependencies(book);
        bookDAO.delete(bookId);
    }

    @Transactional(readOnly = true)
    public BookDetailsDTO getBookDetails(int bookId) {
        Book book = bookDAO.findByIdWithCopies(bookId) // Используем новый метод
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return bookMapper.toDetailsDTO(book);
    }

    @Transactional
    private BookResponseDTO createNewBook(BookAddDTO dto) {
        Book book = bookMapper.toEntity(dto);
        book.setStorageArrivalDate(LocalDateTime.now());
        Book savedBook = bookDAO.save(book);
        bookCopyService.addCopies(savedBook.getId(), dto.getCopiesCount());
        catalogService.addBookToCatalogs(savedBook.getId(), dto.getCatalogIds());
        return bookMapper.toResponseDTO(savedBook);
    }

    private BookResponseDTO updateExistingBook(Book book, BookAddDTO dto) {
        bookCopyService.addCopies(book.getId(), dto.getCopiesCount());
        catalogService.updateCatalogs(book.getId(), dto.getCatalogIds());
        return bookMapper.toResponseDTO(book);
    }

    private BookCopy createNewCopy(Book book) {
        return BookCopy.builder()
                .book(book)
                .status(CopyStatus.AVAILABLE)
                .build();
    }

    @Transactional
    private void deleteDependencies(Book book) {
        catalogService.removeBookFromAllCatalogs(book.getId());

        book.getCopies().forEach(copy ->
                bookCopyDAO.delete(copy.getCopyId()));
    }

    @Transactional(readOnly = true)
    private Book getBookById(int bookId) {
        return bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }


    private void validateDeletion(Book book) {
        if (book.getCopies().stream().anyMatch(c -> c.getStatus() == CopyStatus.RENTED)) {
            throw new IllegalStateException("Book has active rentals");
        }
    }

    private void validateStatusChange(BookCopy copy, CopyStatus newStatus) {
        if (newStatus == CopyStatus.RENTED && copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException("Copy must be available for renting");
        }
    }
}
