package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.book.*;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookService {

    @Autowired
    private BookDAO bookDAO;

    private final CatalogService catalogService;
    private final BookMapper bookMapper;
    private final BookCopyService bookCopyService;

    public BookService(CatalogService catalogService, BookMapper bookMapper, BookCopyService bookCopyService) {
        this.catalogService = catalogService;
        this.bookMapper = bookMapper;
        this.bookCopyService = bookCopyService;
    }

    @Transactional
    public BookResponseDTO addOrUpdateBook(BookAddDTO dto) {
        if (bookDAO.findByIsbn(dto.getIsbn()).isPresent()) {
            return updateExistingBook(dto);
        }
        return createNewBook(dto);
    }


    @Transactional
    public BookResponseDTO updateBook(int bookId, BookUpdateDTO dto) {
        Book book = findBookByIdOrThrow(bookId);
        bookMapper.updateFromDto(dto, book);
        return bookMapper.toResponseDTO(bookDAO.save(book));
    }

    @Transactional
    public void deleteBook(int bookId) {
        Book book = findBookByIdOrThrow(bookId);
        validateDeletion(book);
        bookCopyService.deleteDependencies(book);
        bookDAO.delete(bookId);
    }

    @Transactional(readOnly = true)
    public BookDetailsDTO getBookDetails(int bookId) {
        Book book = bookDAO.findByIdWithCopies(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
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
    @Transactional
    private BookResponseDTO updateExistingBook(BookAddDTO dto) {
        Book book = findBookByIBSNOrThrow(dto);
        bookCopyService.addCopies(book.getId(), dto.getCopiesCount());
        catalogService.updateCatalogs(book.getId(), dto.getCatalogIds());
        return bookMapper.toResponseDTO(book);
    }


    private Book findBookByIBSNOrThrow(BookAddDTO dto) {
        return bookDAO.findByIsbn(dto.getIsbn())
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
    }

    private Book findBookByIdOrThrow(int bookId) {
        return bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
    }

    private void validateDeletion(Book book) {
        if (book.getCopies().stream().anyMatch(c -> c.getStatus() == CopyStatus.RENTED)) {
            throw new IllegalStateException("Book has active rentals");
        }
    }

}
