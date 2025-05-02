package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.book.*;
import com.example.booklibrary.dto.request.bookcopy.BookCopyDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public BookService(BookCopyDAO bookCopyDAO, CatalogService catalogService,  BookMapper bookMapper) {
        this.bookCopyDAO = bookCopyDAO;
        this.catalogService = catalogService;
        this.bookMapper = bookMapper;
    }

    public BookResponseDTO addOrUpdateBook(BookAddDTO dto) {
        return bookDAO.findByIsbn(dto.getIsbn())
                .map(book -> updateExistingBook(book, dto))
                .orElseGet(() -> createNewBook(dto));
    }

    public BookResponseDTO updateBook(int bookId, BookUpdateDTO dto) {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        bookMapper.updateFromDto(dto, book);
        return bookMapper.toResponseDTO(bookDAO.save(book));
    }

    public void deleteBook(int bookId) {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        validateDeletion(book);
        deleteDependencies(book);
        bookDAO.delete(bookId);
    }

    public List<BookCopyDTO> addCopies(int bookId, int count) {
        Book book = getBookById(bookId);
        return IntStream.range(0, count)
                .mapToObj(i -> createNewCopy(book))
                .map(copy -> {
                    bookCopyDAO.save(copy);
                    return copy;
                })
                .map(this::toCopyDTO)
                .collect(Collectors.toList());
    }

    public BookCopyDTO updateCopyStatus(int copyId, CopyStatus status) {
        BookCopy copy = bookCopyDAO.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Copy not found"));
        validateStatusChange(copy, status);
        copy.setStatus(status);
        return toCopyDTO(bookCopyDAO.save(copy));
    }

    public BookDetailsDTO getBookDetails(int bookId) {
        Book book = getBookById(bookId);
        return bookMapper.toDetailsDTO(book);
    }

    public List<BookResponseDTO> searchBooks(String query) {
        return bookDAO.search(query).stream()
                .map(bookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private BookResponseDTO createNewBook(BookAddDTO dto) {
        Book book = bookMapper.toEntity(dto);
        book.setStorageArrivalDate(LocalDateTime.now());
        Book savedBook = bookDAO.save(book);
        addCopies(savedBook.getId(), dto.getCopiesCount());
        catalogService.addBookToCatalogs(savedBook.getId(), dto.getCatalogIds());
        return bookMapper.toResponseDTO(savedBook);
    }

    private BookResponseDTO updateExistingBook(Book book, BookAddDTO dto) {
        addCopies(book.getId(), dto.getCopiesCount());
        catalogService.updateCatalogs(book.getId(), dto.getCatalogIds());
        return bookMapper.toResponseDTO(book);
    }

    private BookCopy createNewCopy(Book book) {
        return BookCopy.builder()
                .book(book)
                .status(CopyStatus.AVAILABLE)
                .build();
    }


    private BookCopyDTO toCopyDTO(BookCopy copy) {
        return BookCopyDTO.builder()
                .copyId(copy.getCopyId())
                .status(copy.getStatus().name())
                .bookTitle(copy.getBook().getBookTitle())
                .build();
    }

    private void deleteDependencies(Book book) {
        catalogService.removeBookFromAllCatalogs(book.getId());

        book.getCopies().forEach(copy ->
                bookCopyDAO.delete(copy.getCopyId()));
    }

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
