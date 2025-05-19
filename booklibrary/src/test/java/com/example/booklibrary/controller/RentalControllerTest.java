package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.rental.RentalCopyDTO;
import com.example.booklibrary.dto.response.rental.*;
import com.example.booklibrary.security.CustomUserDetails;
import com.example.booklibrary.service.RentalService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalControllerTest {

    @InjectMocks
    private RentalController rentalController;

    @Mock
    private RentalService rentalService;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private Authentication authentication;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void rentCopy_success() {
        RentalCopyDTO dto = new RentalCopyDTO(1, 1, LocalDateTime.now().plusDays(10));
        RentalDTO expected = new RentalDTO(1, 1, 1, "Book", LocalDateTime.now(), dto.getDueDate(), null, "RENTED");

        when(rentalService.rentCopy(dto)).thenReturn(expected);

        ResponseEntity<RentalDTO> response = rentalController.rentCopy(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void rentCopy_userNotFound() {
        RentalCopyDTO dto = new RentalCopyDTO(999, 1, LocalDateTime.now().plusDays(10));
        when(rentalService.rentCopy(dto)).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                rentalController.rentCopy(dto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void returnCopy_success() {
        int userId = 1;
        int copyId = 42;

        RentalDTO rentalDTO = new RentalDTO(1, userId, copyId, "Book Title",
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                "RETURNED");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(userId);
        when(rentalService.returnCopy(userId, copyId)).thenReturn(rentalDTO);

        ResponseEntity<RentalDTO> response = rentalController.returnBookCopy(copyId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rentalDTO, response.getBody());

        verify(rentalService).returnCopy(userId, copyId);
    }

    @Test
    void returnCopy_noActiveRental_throwsException() {
        int userId = 1;
        int copyId = 42;

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(userId);
        when(rentalService.returnCopy(userId, copyId))
                .thenThrow(new IllegalStateException("Нет активных аренд"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                rentalController.returnBookCopy(copyId, authentication));

        assertEquals("Нет активных аренд", exception.getMessage());
        verify(rentalService).returnCopy(userId, copyId);
    }

    @Test
    void getOverdueRentals_success() {
        RentalLateResponseDTO dto = new RentalLateResponseDTO(1, 1, "Book",
                LocalDateTime.now().minusDays(5), null, 5);
        when(rentalService.findOverdueRentals()).thenReturn(List.of(dto));

        ResponseEntity<List<RentalLateResponseDTO>> response = rentalController.getOverdueRentals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getOverdueRentals_emptyList() {
        when(rentalService.findOverdueRentals()).thenReturn(Collections.emptyList());

        ResponseEntity<List<RentalLateResponseDTO>> response = rentalController.getOverdueRentals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void markOverdueRentals_success() {
        doNothing().when(rentalService).markOverdueRentalsAsLate();

        ResponseEntity<Void> response = rentalController.markOverdueRentals();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void markOverdueRentals_exception() {
        doThrow(new RuntimeException("Ошибка при пометке")).when(rentalService).markOverdueRentalsAsLate();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                rentalController.markOverdueRentals());
        assertEquals("Ошибка при пометке", exception.getMessage());
    }

    @Test
    void getUserRentalHistory_success() {
        RentalUserHistoryResponseDTO dto = new RentalUserHistoryResponseDTO(1, "Book", "Author",
                LocalDateTime.now(), LocalDateTime.now().plusDays(10), null, "RENTED");

        when(rentalService.getUserRentalHistory(1)).thenReturn(List.of(dto));

        ResponseEntity<List<RentalUserHistoryResponseDTO>> response = rentalController.getUserRentalHistory(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getUserRentalHistory_notFound() {
        when(rentalService.getUserRentalHistory(999)).thenThrow(new EntityNotFoundException("Пользователь не найден"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                rentalController.getUserRentalHistory(999));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getCopyRentalHistory_success() {
        RentalCopyStoryResponseDTO dto = new RentalCopyStoryResponseDTO(1, 1, "user", "RETURNED",
                "Book", LocalDateTime.now(), LocalDateTime.now().plusDays(7), LocalDateTime.now());

        when(rentalService.getCopyRentalHistory(1)).thenReturn(List.of(dto));

        ResponseEntity<List<RentalCopyStoryResponseDTO>> response = rentalController.getCopyRentalHistory(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getCopyRentalHistory_notFound() {
        when(rentalService.getCopyRentalHistory(999)).thenThrow(new EntityNotFoundException("Копия не найдена"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                rentalController.getCopyRentalHistory(999));
        assertEquals("Копия не найдена", exception.getMessage());
    }
}
