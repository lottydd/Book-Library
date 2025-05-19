package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.bookcopy.BookAddCopyDTO;
import com.example.booklibrary.dto.request.bookcopy.BookCopyUpdateDTO;
import com.example.booklibrary.dto.response.bookcopy.BookCopyDTO;
import com.example.booklibrary.service.BookCopyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCopyControllerTest {

    @Mock
    private BookCopyService bookCopyService;

    @InjectMocks
    private BookCopyController bookCopyController;

    private BookAddCopyDTO addCopyDTO;
    private BookCopyUpdateDTO updateDTO;
    private BookCopyDTO bookCopyDTO;

    @BeforeEach
    void setUp() {
        addCopyDTO = new BookAddCopyDTO(1, 3);
        updateDTO = new BookCopyUpdateDTO(10, com.example.booklibrary.util.CopyStatus.RENTED);
        bookCopyDTO = BookCopyDTO.builder()
                .copyId(10)
                .status("RENTED")
                .bookTitle("Test Book")
                .build();
    }

    @Test
    void addCopies_ShouldReturnCreated() {
        doNothing().when(bookCopyService).addCopies(addCopyDTO);

        ResponseEntity<Void> response = bookCopyController.addCopies(addCopyDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(bookCopyService, times(1)).addCopies(addCopyDTO);
    }

    @Test
    void addCopies_ShouldThrowException_ReturnInternalServerError() {
        doThrow(new RuntimeException("Ошибка при добавлении")).when(bookCopyService).addCopies(addCopyDTO);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookCopyController.addCopies(addCopyDTO));
        assertEquals("Ошибка при добавлении", ex.getMessage());

        verify(bookCopyService, times(1)).addCopies(addCopyDTO);
    }

    @Test
    void deleteBookCopies_ShouldReturnNoContent() {
        Integer bookId = 5;

        doNothing().when(bookCopyService).deleteBookCopies(any(RequestIdDTO.class));

        ResponseEntity<Void> response = bookCopyController.deleteBookCopies(bookId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookCopyService, times(1)).deleteBookCopies(argThat(dto -> dto.getId().equals(bookId)));
    }

    @Test
    void deleteBookCopies_ShouldThrowException_ReturnInternalServerError() {
        Integer bookId = 5;
        doThrow(new RuntimeException("Ошибка при удалении")).when(bookCopyService).deleteBookCopies(any(RequestIdDTO.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookCopyController.deleteBookCopies(bookId));
        assertEquals("Ошибка при удалении", ex.getMessage());

        verify(bookCopyService, times(1)).deleteBookCopies(argThat(dto -> dto.getId().equals(bookId)));
    }

    @Test
    void updateCopyStatus_ShouldReturnUpdatedCopyDTO() {
        when(bookCopyService.updateCopyStatus(updateDTO)).thenReturn(bookCopyDTO);

        ResponseEntity<BookCopyDTO> response = bookCopyController.updateCopyStatus(updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookCopyDTO.getCopyId(), response.getBody().getCopyId());
        assertEquals(bookCopyDTO.getStatus(), response.getBody().getStatus());
        verify(bookCopyService, times(1)).updateCopyStatus(updateDTO);
    }

    @Test
    void updateCopyStatus_ShouldThrowEntityNotFoundException() {
        when(bookCopyService.updateCopyStatus(updateDTO)).thenThrow(new EntityNotFoundException("Копия не найдена"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> bookCopyController.updateCopyStatus(updateDTO));
        assertEquals("Копия не найдена", ex.getMessage());

        verify(bookCopyService, times(1)).updateCopyStatus(updateDTO);
    }

    @Test
    void rentedCopiesInfo_ShouldReturnListOfBookCopyDTO() {
        Integer bookId = 1;
        List<BookCopyDTO> mockList = List.of(bookCopyDTO);

        when(bookCopyService.getRentedCopies(any(RequestIdDTO.class))).thenReturn(mockList);

        ResponseEntity<List<BookCopyDTO>> response = bookCopyController.rentedCopiesInfo(bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(bookCopyService, times(1)).getRentedCopies(argThat(dto -> dto.getId().equals(bookId)));
    }

    @Test
    void rentedCopiesInfo_ShouldThrowException() {
        Integer bookId = 1;

        when(bookCopyService.getRentedCopies(any(RequestIdDTO.class))).thenThrow(new RuntimeException("Ошибка при получении"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookCopyController.rentedCopiesInfo(bookId));
        assertEquals("Ошибка при получении", ex.getMessage());

        verify(bookCopyService, times(1)).getRentedCopies(argThat(dto -> dto.getId().equals(bookId)));
    }
}
