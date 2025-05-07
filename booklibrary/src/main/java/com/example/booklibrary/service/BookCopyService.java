package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.bookcopy.BookCopyDTO;
import com.example.booklibrary.mapper.BookCopyMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
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


    public BookCopyService(BookCopyDAO bookCopyDAO, BookCopyMapper bookCopyMapper, BookDAO bookDAO) {
        this.bookCopyDAO = bookCopyDAO;
        this.bookCopyMapper = bookCopyMapper;
        this.bookDAO = bookDAO;
    }

    @Transactional
    public void addCopies(int bookId, int count) {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            copies.add(BookCopy.builder()
                    .book(book)
                    .status(CopyStatus.AVAILABLE)
                    .build());
        }
        bookCopyDAO.saveAll(copies);
    }
    @Transactional
    public BookCopyDTO updateCopyStatus(int copyId, CopyStatus status) {
        BookCopy copy = getCopyById(copyId);
        validateStatusChange(copy, status);
        copy.setStatus(status);
        return bookCopyMapper.toDto(bookCopyDAO.save(copy));
    }

    @Transactional
    public void deleteAllCopiesForBook(int bookId) {
        bookCopyDAO.deleteByBookId(bookId); // NEW: Оптимизированный массовый delete
    }
    @Transactional
    public boolean hasRentedCopies(int bookId) {
        return bookCopyDAO.existsByBookIdAndStatus(bookId, CopyStatus.RENTED);
    }

    private BookCopy createNewCopy(Book book) {
        return BookCopy.builder()
                .book(book)
                .status(CopyStatus.AVAILABLE)
                .build();
    }

    private BookCopy getCopyById(int copyId) {
        return bookCopyDAO.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Copy not found"));
    }

    private void validateStatusChange(BookCopy copy, CopyStatus newStatus) {
        if (newStatus == CopyStatus.RENTED && copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException("Copy must be available for renting");
        }
    }


}
