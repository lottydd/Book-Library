package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.book.BookCreateDTO;
import com.example.booklibrary.dto.request.book.BookUpdateDTO;
import com.example.booklibrary.dto.response.book.BookDetailsDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private BookCreateDTO createDTO;
    private BookUpdateDTO updateDTO;
    private BookResponseDTO responseDTO;
    private BookDetailsDTO detailsDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

        responseDTO = new BookResponseDTO();

        detailsDTO = new BookDetailsDTO();
    }


    @Test
    void createBook_shouldReturnCreatedResponse() {
        when(bookService.createBook(createDTO)).thenReturn(responseDTO);

        ResponseEntity<BookResponseDTO> response = bookController.createBook(createDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(bookService, times(1)).createBook(createDTO);
    }

    @Test
    void createBook_shouldThrowException() {
        when(bookService.createBook(createDTO)).thenThrow(new RuntimeException("Ошибка создания"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookController.createBook(createDTO));
        assertEquals("Ошибка создания", ex.getMessage());
        verify(bookService, times(1)).createBook(createDTO);
    }


    @Test
    void updateBook_shouldReturnOkResponse() {
        when(bookService.updateBook(updateDTO)).thenReturn(responseDTO);

        ResponseEntity<BookResponseDTO> response = bookController.updateBook(updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(bookService, times(1)).updateBook(updateDTO);
    }

    @Test
    void updateBook_shouldThrowEntityNotFoundException() {
        when(bookService.updateBook(updateDTO)).thenThrow(new EntityNotFoundException("Книга не найдена"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookController.updateBook(updateDTO));
        assertEquals("Книга не найдена", ex.getMessage());
        verify(bookService, times(1)).updateBook(updateDTO);
    }


    @Test
    void deleteBook_shouldReturnNoContent() {
        doNothing().when(bookService).deleteBook(any(RequestIdDTO.class));

        ResponseEntity<Void> response = bookController.deleteBook(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).deleteBook(argThat(dto -> dto.getId() == 1));
    }

    @Test
    void deleteBook_shouldThrowException() {
        doThrow(new RuntimeException("Ошибка удаления")).when(bookService).deleteBook(any(RequestIdDTO.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookController.deleteBook(1));
        assertEquals("Ошибка удаления", ex.getMessage());
        verify(bookService, times(1)).deleteBook(argThat(dto -> dto.getId() == 1));
    }


    @Test
    void getBookDetails_shouldReturnOkResponse() {
        when(bookService.getBookDetails(any(RequestIdDTO.class))).thenReturn(detailsDTO);

        ResponseEntity<BookDetailsDTO> response = bookController.getBookDetails(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(detailsDTO, response.getBody());
        verify(bookService, times(1)).getBookDetails(argThat(dto -> dto.getId() == 1));
    }

    @Test
    void getBookDetails_shouldThrowEntityNotFoundException() {
        when(bookService.getBookDetails(any(RequestIdDTO.class))).thenThrow(new EntityNotFoundException("Книга не найдена"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookController.getBookDetails(1));
        assertEquals("Книга не найдена", ex.getMessage());
        verify(bookService, times(1)).getBookDetails(argThat(dto -> dto.getId() == 1));
    }
}
