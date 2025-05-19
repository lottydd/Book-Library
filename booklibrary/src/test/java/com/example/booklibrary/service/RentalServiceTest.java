package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.RentalDAO;
import com.example.booklibrary.dao.UserDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.rental.RentalCopyDTO;
import com.example.booklibrary.dto.response.rental.*;
import com.example.booklibrary.mapper.RentalMapper;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.model.Rental;
import com.example.booklibrary.model.User;
import com.example.booklibrary.util.CopyStatus;
import com.example.booklibrary.util.RentalStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class RentalServiceTest {

    @InjectMocks
    private RentalService rentalService;

    @Mock
    private RentalDAO rentalDAO;
    @Mock
    private BookCopyDAO bookCopyDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private RentalMapper rentalMapper;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void rentCopy_success() {
        RentalCopyDTO dto = new RentalCopyDTO(1, 1, now.plusDays(14));
        User user = new User();
        BookCopy copy = new BookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);
        Rental rental = Rental.builder().id(1).user(user).copy(copy).status(RentalStatus.RENTED).build();
        RentalDTO rentalDTO = RentalDTO.builder().rentalId(1).build();

        when(userDAO.findById(1)).thenReturn(Optional.of(user));
        when(bookCopyDAO.findById(1)).thenReturn(Optional.of(copy));
        when(bookCopyDAO.update(any())).thenReturn(copy);
        when(rentalDAO.save(any())).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDTO);

        RentalDTO result = rentalService.rentCopy(dto);

        assertNotNull(result);
        assertEquals(1, result.getRentalId());
    }

    @Test
    void rentCopy_userNotFound() {
        RentalCopyDTO dto = new RentalCopyDTO(99, 1, now.plusDays(14));
        when(userDAO.findById(99)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> rentalService.rentCopy(dto));
        assertTrue(ex.getMessage().contains("User not found"));
    }


     @Test
     void returnCopy_Success() {
         int userId = 1;
         int copyId = 100;

         BookCopy copy = new BookCopy();
         copy.setCopyId(copyId);
         copy.setStatus(CopyStatus.RENTED);

         User user = new User();
         user.setId(userId);

         Rental rental = new Rental();
         rental.setId(200);
         rental.setUser(user);
         rental.setCopy(copy);
         rental.setStatus(RentalStatus.RENTED);

         Rental updatedRental = new Rental();
         updatedRental.setId(200);
         updatedRental.setUser(user);
         updatedRental.setCopy(copy);
         updatedRental.setStatus(RentalStatus.RETURNED);
         updatedRental.setReturnDate(LocalDateTime.now());

         RentalDTO rentalDTO = new RentalDTO();

         when(bookCopyDAO.findById(copyId)).thenReturn(Optional.of(copy));
         when(rentalDAO.findActiveRentalByCopyId(copyId)).thenReturn(Optional.of(rental));
         when(bookCopyDAO.update(copy)).thenReturn(copy);
         when(rentalDAO.update(any(Rental.class))).thenReturn(updatedRental);
         when(rentalMapper.toDto(updatedRental)).thenReturn(rentalDTO);

         RentalDTO result = rentalService.returnCopy(userId, copyId);

         assertEquals(rentalDTO, result);
         assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
         assertEquals(RentalStatus.RETURNED, updatedRental.getStatus());
         verify(bookCopyDAO).update(copy);
         verify(rentalDAO).update(rental);
         verify(rentalMapper).toDto(updatedRental);
     }

     @Test
     void returnCopy_CopyNotFound_ShouldThrowException() {
         int userId = 1;
         int copyId = 100;

         when(bookCopyDAO.findById(copyId)).thenReturn(Optional.empty());

         assertThrows(EntityNotFoundException.class, () -> rentalService.returnCopy(userId, copyId));
         verify(bookCopyDAO, never()).update(any());
         verify(rentalDAO, never()).update(any());
     }

     @Test
     void returnCopy_ActiveRentalNotFound_ShouldThrowException() {
         int userId = 1;
         int copyId = 100;

         BookCopy copy = new BookCopy();
         copy.setCopyId(copyId);

         when(bookCopyDAO.findById(copyId)).thenReturn(Optional.of(copy));
         when(rentalDAO.findActiveRentalByCopyId(copyId)).thenReturn(Optional.empty());

         assertThrows(IllegalStateException.class, () -> rentalService.returnCopy(userId, copyId));
         verify(bookCopyDAO, never()).update(any());
         verify(rentalDAO, never()).update(any());
     }

     @Test
     void returnCopy_UserNotOwner_ShouldThrowException() {
         int userId = 1;
         int copyId = 100;

         BookCopy copy = new BookCopy();
         copy.setCopyId(copyId);

         User rentalUser = new User();
         rentalUser.setId(2);

         Rental rental = new Rental();
         rental.setUser(rentalUser);
         rental.setCopy(copy);

         when(bookCopyDAO.findById(copyId)).thenReturn(Optional.of(copy));
         when(rentalDAO.findActiveRentalByCopyId(copyId)).thenReturn(Optional.of(rental));

         assertThrows(SecurityException.class, () -> rentalService.returnCopy(userId, copyId));
         verify(bookCopyDAO, never()).update(any());
         verify(rentalDAO, never()).update(any());
     }


    @Test
    void findOverdueRentals_success() {
        Rental rental = new Rental();
        RentalLateResponseDTO dto = new RentalLateResponseDTO();
        when(rentalDAO.findOverdueRentals(any())).thenReturn(List.of(rental));
        when(rentalMapper.toLateDto(rental)).thenReturn(dto);

        List<RentalLateResponseDTO> result = rentalService.findOverdueRentals();
        assertEquals(1, result.size());
    }

    @Test
    void findOverdueRentals_empty() {
        when(rentalDAO.findOverdueRentals(any())).thenReturn(Collections.emptyList());

        List<RentalLateResponseDTO> result = rentalService.findOverdueRentals();
        assertTrue(result.isEmpty());
    }


    @Test
    void markOverdueRentalsAsLate_success() {
        Rental rental = new Rental();
        rental.setId(1);
        rental.setStatus(RentalStatus.RENTED);
        when(rentalDAO.findOverdueRentals(any())).thenReturn(List.of(rental));

        assertDoesNotThrow(() -> rentalService.markOverdueRentalsAsLate());
        verify(rentalDAO, times(1)).update(any());
    }

    @Test
    void markOverdueRentalsAsLate_emptyList() {
        when(rentalDAO.findOverdueRentals(any())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> rentalService.markOverdueRentalsAsLate());
        verify(rentalDAO, never()).update(any());
    }


    @Test
    void getUserRentalHistory_success() {
        Rental rental = new Rental();
        RentalUserHistoryResponseDTO dto = new RentalUserHistoryResponseDTO();
        when(rentalDAO.findByUserId(1)).thenReturn(List.of(rental));
        when(rentalMapper.toUserHistoryDtoList(List.of(rental))).thenReturn(List.of(dto));

        List<RentalUserHistoryResponseDTO> result = rentalService.getUserRentalHistory(1);
        assertEquals(1, result.size());
    }

    @Test
    void getUserRentalHistory_empty() {
        when(rentalDAO.findByUserId(1)).thenReturn(Collections.emptyList());
        when(rentalMapper.toUserHistoryDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RentalUserHistoryResponseDTO> result = rentalService.getUserRentalHistory(1);
        assertTrue(result.isEmpty());
    }


    @Test
    void getCopyRentalHistory_success() {
        Rental rental = new Rental();
        RentalCopyStoryResponseDTO dto = new RentalCopyStoryResponseDTO();
        when(rentalDAO.findByCopyId(1)).thenReturn(List.of(rental));
        when(rentalMapper.toCopyStoryDtoList(List.of(rental))).thenReturn(List.of(dto));

        List<RentalCopyStoryResponseDTO> result = rentalService.getCopyRentalHistory(1);
        assertEquals(1, result.size());
    }

    @Test
    void getCopyRentalHistory_empty() {
        when(rentalDAO.findByCopyId(1)).thenReturn(Collections.emptyList());
        when(rentalMapper.toCopyStoryDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<RentalCopyStoryResponseDTO> result = rentalService.getCopyRentalHistory(1);
        assertTrue(result.isEmpty());
    }
}