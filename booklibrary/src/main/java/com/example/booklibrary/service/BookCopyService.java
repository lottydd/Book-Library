package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.bookcopy.BookAddCopyDTO;
import com.example.booklibrary.dto.response.bookcopy.BookCopyDTO;
import com.example.booklibrary.dto.request.bookcopy.BookCopyUpdateDTO;
import com.example.booklibrary.mapper.BookCopyMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service

public class BookCopyService {

    private final BookCopyDAO bookCopyDAO;
    private final BookCopyMapper bookCopyMapper;
    private final BookDAO bookDAO;

    private static final Logger logger = LoggerFactory.getLogger(BookCopyService.class);

    @Autowired
    public BookCopyService(BookCopyDAO bookCopyDAO, BookCopyMapper bookCopyMapper, BookDAO bookDAO) {
        this.bookCopyDAO = bookCopyDAO;
        this.bookCopyMapper = bookCopyMapper;
        this.bookDAO = bookDAO;
    }

    @Transactional
    public void addCopies(BookAddCopyDTO dto) {
        logger.debug("Попытка добавления {} копий для книги {}", dto.getCount(), dto.getBookId());
        Book book = bookDAO.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
        logger.debug("Книга найдена: ID: {}, Название: '{}'",
                book.getId(), book.getBookTitle());
        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < dto.getCount(); i++) {
            copies.add(BookCopy.builder()
                    .book(book)
                    .status(CopyStatus.AVAILABLE)
                    .build());
        }
        logger.debug("Создано {} новых копий. Статус: {}",
                copies.size(), CopyStatus.AVAILABLE);

        bookCopyDAO.saveAll(copies);
        logger.info("Успешно добавлено {} копий для книги ID: {}",
                dto.getCount(),
                dto.getBookId());
    }

    @Transactional
    public void addCopiesForNewBook(int bookCount, Book newBook) {
        logger.debug("Попытка добавления  {} копий для книги  {}", bookCount, newBook.getBookTitle());

        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < bookCount; i++) {
            copies.add(BookCopy.builder()
                    .book(newBook)
                    .status(CopyStatus.AVAILABLE)
                    .build());
        }
        logger.debug("Создано  {} новых копий. Статус: {}",
                copies.size(), CopyStatus.AVAILABLE);

        bookCopyDAO.saveAll(copies);
        logger.info("Успешно  добавлено {} копий для книги ID: {}",
                bookCount,
                newBook.getBookTitle());
    }

    //ADMIN
    @Transactional
    public BookCopyDTO updateCopyStatus(BookCopyUpdateDTO dto) {
        logger.debug("Попытка обновления статуса {} копии  {}", dto.getStatus(), dto.getCopyId());
        BookCopy copy = getCopyById(dto.getCopyId());
        validateStatusChange(copy, dto.getStatus());
        copy.setStatus(dto.getStatus());
        logger.debug("Cтатус копии обновлен: {}", dto.getStatus());
        return bookCopyMapper.toDto(bookCopyDAO.save(copy));
    }

    private void validateStatusChange(BookCopy copy, CopyStatus newStatus) {
        if (newStatus == CopyStatus.RENTED && copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException("Копия должна быть доступна для аренды");
        }
    }

    @Transactional
    public void deleteBookCopies(RequestIdDTO dto) {
        bookCopyDAO.deleteByBookId(dto.getId());
    }

    @Transactional
    public boolean hasRentedCopies(RequestIdDTO dto) {
        return bookCopyDAO.existsByBookIdAndStatus(dto.getId(), CopyStatus.RENTED);
    }

    @Transactional(readOnly = true)
    public List<BookCopyDTO> getRentedCopies(RequestIdDTO dto) {
        List<BookCopy> rentedCopies = bookCopyDAO.findRentedCopiesByBookId(dto.getId());
        return rentedCopies.stream()
                .map(bookCopyMapper::toDto)
                .toList();
    }

    @Transactional
    private BookCopy getCopyById(int copyId) {
        return bookCopyDAO.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Копия не найдена"));
    }


}
