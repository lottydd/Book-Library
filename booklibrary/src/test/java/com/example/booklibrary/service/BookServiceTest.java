package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.book.BookCreateDTO;
import com.example.booklibrary.dto.request.book.BookUpdateDTO;
import com.example.booklibrary.dto.response.book.BookDetailsDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookDAO bookDAO;

    @Mock
    private CatalogService catalogService;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookCopyService bookCopyService;

    @InjectMocks
    private BookService bookService;

    private BookCreateDTO createDTO;
    private BookUpdateDTO updateDTO;
    private Book bookEntity;

    @BeforeEach
    void setup() {
        createDTO = new BookCreateDTO();
        createDTO.setAuthor("Author");
        createDTO.setBookTitle("Title");
        createDTO.setIsbn("123-ISBN");
        createDTO.setPublicationYear(2020);
        createDTO.setDescription("Description");
        createDTO.setCopiesCount(2);

        updateDTO = new BookUpdateDTO();
        updateDTO.setAuthor("Author Updated");
        updateDTO.setBookTitle("Title Updated");
        updateDTO.setIsbn("123-ISBN");
        updateDTO.setPublicationYear(2021);
        updateDTO.setDescription("Description Updated");

        bookEntity = new Book();
        bookEntity.setId(1);
        bookEntity.setAuthor("Author");
        bookEntity.setBookTitle("Title");
        bookEntity.setIsbn("123-ISBN");
        bookEntity.setPublicationYear(2020);
        bookEntity.setDescription("Description");
        bookEntity.setStorageArrivalDate(LocalDateTime.now());
        bookEntity.setCopies(new ArrayList<>());
    }


    @Test
    void createBook_success() {
        when(bookDAO.findByIsbn(createDTO.getIsbn())).thenReturn(Optional.empty());
        when(bookMapper.toEntity(createDTO)).thenReturn(bookEntity);
        when(bookDAO.save(bookEntity)).thenReturn(bookEntity);
        when(bookMapper.toResponseDTO(bookEntity)).thenReturn(new BookResponseDTO());

        BookResponseDTO response = bookService.createBook(createDTO);

        assertNotNull(response);
        verify(bookCopyService).addCopiesForNewBook(createDTO.getCopiesCount(), bookEntity);
        verify(bookDAO).save(bookEntity);
    }

    @Test
    void createBook_alreadyExists_throws() {
        when(bookDAO.findByIsbn(createDTO.getIsbn())).thenReturn(Optional.of(bookEntity));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookService.createBook(createDTO));
        assertEquals("Книга с таким ISBN уже существует", ex.getMessage());

        verify(bookDAO, never()).save(any());
        verify(bookCopyService, never()).addCopiesForNewBook(anyInt(), any());
    }


    @Test
    void updateBook_success() {
        when(bookDAO.findByIsbn(updateDTO.getIsbn())).thenReturn(Optional.of(bookEntity));
        when(bookDAO.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookMapper.toResponseDTO(any(Book.class))).thenReturn(new BookResponseDTO());

        BookResponseDTO response = bookService.updateBook(updateDTO);

        assertNotNull(response);
        verify(bookDAO).save(bookEntity);
        assertEquals(updateDTO.getAuthor(), bookEntity.getAuthor());
        assertEquals(updateDTO.getBookTitle(), bookEntity.getBookTitle());
    }

    @Test
    void updateBook_notFound_throws() {
        when(bookDAO.findByIsbn(updateDTO.getIsbn())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(updateDTO));
        assertEquals("Книга не найдена", ex.getMessage());

        verify(bookDAO, never()).save(any());
    }


    @Test
    void deleteBook_success() {
        Book bookWithNoRentedCopies = new Book();
        bookWithNoRentedCopies.setCopies(new ArrayList<>());

        when(bookDAO.findById(1)).thenReturn(Optional.of(bookWithNoRentedCopies));
        doNothing().when(catalogService).removeBookFromAllCatalogs(1);
        doNothing().when(bookCopyService).deleteBookCopies(any(RequestIdDTO.class));
        doNothing().when(bookDAO).delete(1);

        bookService.deleteBook(new RequestIdDTO(1));

        verify(catalogService).removeBookFromAllCatalogs(1);
        verify(bookCopyService).deleteBookCopies(any(RequestIdDTO.class));
        verify(bookDAO).delete(1);
    }

    @Test
    void deleteBook_withRentedCopies_throws() {
        Book bookWithRentedCopy = new Book();
        bookWithRentedCopy.setCopies(List.of(new com.example.booklibrary.model.BookCopy() {{
            setStatus(CopyStatus.RENTED);
        }}));

        when(bookDAO.findById(1)).thenReturn(Optional.of(bookWithRentedCopy));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookService.deleteBook(new RequestIdDTO(1)));
        assertEquals("Невозможно удалить книгу, т.к. у книги существуют активные Аренды", ex.getMessage());

        verify(catalogService, never()).removeBookFromAllCatalogs(anyInt());
        verify(bookCopyService, never()).deleteBookCopies(any());
        verify(bookDAO, never()).delete(anyInt());
    }


    @Test
    void getBookDetails_success() {
        when(bookDAO.findByIdWithCopies(1)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toDetailsDTO(bookEntity)).thenReturn(new BookDetailsDTO());

        BookDetailsDTO details = bookService.getBookDetails(new RequestIdDTO(1));

        assertNotNull(details);
        verify(bookDAO).findByIdWithCopies(1);
        verify(bookMapper).toDetailsDTO(bookEntity);
    }

    @Test
    void getBookDetails_notFound_throws() {
        when(bookDAO.findByIdWithCopies(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookService.getBookDetails(new RequestIdDTO(1)));
        assertEquals("Книга не найдена", ex.getMessage());
    }
}