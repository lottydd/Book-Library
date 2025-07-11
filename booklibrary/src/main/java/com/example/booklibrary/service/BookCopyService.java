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
        logger.info("Попытка добавления {} копий для книги {}", dto.getCount(), dto.getBookId());
        Book book = bookDAO.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));
        logger.info("Книга найдена: ID: {}, Название: '{}'",
                book.getId(), book.getBookTitle());
        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < dto.getCount(); i++) {
            copies.add(BookCopy.builder()
                    .book(book)
                    .status(CopyStatus.AVAILABLE)
                    .build());
        }
        logger.info("Создано {} новых копий. Статус: {}",
                copies.size(), CopyStatus.AVAILABLE);

        bookCopyDAO.saveAll(copies);
        logger.info("Успешно добавлено {} копий для книги ID: {}",
                dto.getCount(),
                dto.getBookId());
    }

    @Transactional
    public void addCopiesForNewBook(int bookCount, Book newBook) {
        logger.info("Попытка добавления  {} копий для книги  {}", bookCount, newBook.getBookTitle());

        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < bookCount; i++) {
            copies.add(BookCopy.builder()
                    .book(newBook)
                    .status(CopyStatus.AVAILABLE)
                    .build());
        }
        logger.info("Создано  {} новых копий. Статус: {}",
                copies.size(), CopyStatus.AVAILABLE);

        bookCopyDAO.saveAll(copies);
        logger.info("Успешно  добавлено {} копий для книги ID: {}",
                bookCount,
                newBook.getBookTitle());
    }

    @Transactional
    public BookCopyDTO updateCopyStatus(BookCopyUpdateDTO dto) {
        logger.info("Попытка обновления статуса {} копии  {}", dto.getStatus(), dto.getCopyId());
        BookCopy copy = getCopyById(dto.getCopyId());
        validateStatusChange(copy, dto.getStatus());
        copy.setStatus(dto.getStatus());
        logger.info("Cтатус копии обновлен: {}", dto.getStatus());
        return bookCopyMapper.toDto(bookCopyDAO.save(copy));
    }

    private void validateStatusChange(BookCopy copy, CopyStatus newStatus) {
        if (newStatus == CopyStatus.RENTED && copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException("Копия должна быть доступна для аренды");
        }
    }

    @Transactional
    public void deleteBookCopies(RequestIdDTO dto) {
        logger.info("Проверка возможности удаления копий книги с ID {}", dto.getId());
        List<BookCopy> nonDeletableCopies = bookCopyDAO.findNonDeletableCopiesByBookId(dto.getId());
        if (!nonDeletableCopies.isEmpty()) {
            logger.error("Удаление невозможно — найдены арендованные или недоступные копии книги. BookID: {}", dto.getId());
            throw new IllegalStateException("Невозможно удалить — некоторые копии книги сейчас арендованы или недоступны");
        }
        bookCopyDAO.deleteByBookId(dto.getId());
    }

    @Transactional(readOnly = true)
    public List<BookCopyDTO> getRentedCopies(RequestIdDTO dto) {
        logger.info("Попытка получения арендованных копий книги {}", dto.getId());
        List<BookCopy> rentedCopies = bookCopyDAO.findRentedCopiesByBookId(dto.getId());
        logger.info("Найдено {} арендованных копий книги ", rentedCopies.size());
        return rentedCopies.stream()
                .map(bookCopyMapper::toDto)
                .toList();
    }

    @Transactional
    private BookCopy getCopyById(int copyId) {
        logger.info("Поиск копии книги по ID: {}", copyId);
        return bookCopyDAO.findById(copyId)
                .orElseThrow(() -> {
                    logger.error("Копия книги с ID {} не найдена", copyId);
                    return new EntityNotFoundException("Копия не найдена");
                });
    }
}
