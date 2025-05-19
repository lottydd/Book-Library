package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.bookcopy.BookAddCopyDTO;
import com.example.booklibrary.dto.request.bookcopy.BookCopyUpdateDTO;
import com.example.booklibrary.dto.response.bookcopy.BookCopyDTO;
import com.example.booklibrary.mapper.BookCopyMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class BookCopyServiceTest {

    @Mock
    private BookCopyDAO bookCopyDAO;

    @Mock
    private BookCopyMapper bookCopyMapper;

    @Mock
    private BookDAO bookDAO;

    @InjectMocks
    private BookCopyService bookCopyService;

    @Test
    void testAddCopies_successful() {
        BookAddCopyDTO dto = new BookAddCopyDTO(1, 3);
        Book book = new Book();
        book.setId(1);
        book.setBookTitle("Test Book");

        when(bookDAO.findById(1)).thenReturn(Optional.of(book));

        bookCopyService.addCopies(dto);

        verify(bookCopyDAO, times(1)).saveAll(anyList());
    }

    @Test
    void testAddCopies_bookNotFound_throwsException() {
        BookAddCopyDTO dto = new BookAddCopyDTO(999, 3);
        when(bookDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookCopyService.addCopies(dto));
    }

    @Test
    void testAddCopiesForNewBook_successful() {
        int numberOfCopies = 3;
        Book newBook = new Book();
        newBook.setId(1);
        newBook.setBookTitle("New Book");

        ArgumentCaptor<List<BookCopy>> captor = ArgumentCaptor.forClass(List.class);

        bookCopyService.addCopiesForNewBook(numberOfCopies, newBook);

        verify(bookCopyDAO).saveAll(captor.capture());
        List<BookCopy> savedCopies = captor.getValue();

        assertEquals(numberOfCopies, savedCopies.size());
        for (BookCopy copy : savedCopies) {
            assertEquals(newBook, copy.getBook());
            assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        }
    }

    @Test
    void testUpdateCopyStatus_successful() {
        BookCopy copy = new BookCopy();
        copy.setCopyId(1);
        copy.setStatus(CopyStatus.AVAILABLE);

        BookCopyUpdateDTO dto = new BookCopyUpdateDTO(1, CopyStatus.RENTED);

        when(bookCopyDAO.findById(1)).thenReturn(Optional.of(copy));
        when(bookCopyDAO.save(any())).thenReturn(copy);
        when(bookCopyMapper.toDto(copy)).thenReturn(new BookCopyDTO());

        BookCopyDTO result = bookCopyService.updateCopyStatus(dto);
        assertNotNull(result);
        verify(bookCopyDAO).save(copy);
    }

    @Test
    void testUpdateCopyStatus_copyNotFound() {
        BookCopyUpdateDTO dto = new BookCopyUpdateDTO(999, CopyStatus.RENTED);
        when(bookCopyDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookCopyService.updateCopyStatus(dto));
    }

    @Test
    void testUpdateCopyStatus_invalidTransition() {
        BookCopy copy = new BookCopy();
        copy.setCopyId(1);
        copy.setStatus(CopyStatus.RENTED);

        BookCopyUpdateDTO dto = new BookCopyUpdateDTO(1, CopyStatus.RENTED);
        when(bookCopyDAO.findById(1)).thenReturn(Optional.of(copy));

        assertThrows(IllegalStateException.class, () -> bookCopyService.updateCopyStatus(dto));
    }

    @Test
    void testDeleteBookCopies_successful() {
        RequestIdDTO dto = new RequestIdDTO(1);
        bookCopyService.deleteBookCopies(dto);
        verify(bookCopyDAO).deleteByBookId(1);
    }

    @Test
    void testGetRentedCopies_returnsList() {
        List<BookCopy> copies = List.of(new BookCopy(), new BookCopy());
        when(bookCopyDAO.findRentedCopiesByBookId(1)).thenReturn(copies);
        when(bookCopyMapper.toDto(any())).thenReturn(new BookCopyDTO());

        List<BookCopyDTO> result = bookCopyService.getRentedCopies(new RequestIdDTO(1));
        assertEquals(2, result.size());
    }

    @Test
    void testGetRentedCopies_returnsEmptyList() {
        when(bookCopyDAO.findRentedCopiesByBookId(1)).thenReturn(new ArrayList<>());

        List<BookCopyDTO> result = bookCopyService.getRentedCopies(new RequestIdDTO(1));
        assertTrue(result.isEmpty());
    }
}
