package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.RentalDAO;
import com.example.booklibrary.dao.UserDAO;
import com.example.booklibrary.dto.request.rental.RentalDTO;
import com.example.booklibrary.mapper.RentalMapper;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.model.Rental;
import com.example.booklibrary.model.User;
import com.example.booklibrary.util.CopyStatus;
import com.example.booklibrary.util.RentalStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class RentalService {


    private final RentalDAO rentalDAO;
    private final BookCopyDAO bookCopyDAO;
    private final UserDAO userDAO;
    private final RentalMapper rentalMapper;

    public RentalService(RentalDAO rentalDAO, BookCopyDAO bookCopyDAO, UserDAO userDAO, RentalMapper rentalMapper) {
        this.rentalDAO = rentalDAO;
        this.bookCopyDAO = bookCopyDAO;
        this.userDAO = userDAO;
        this.rentalMapper = rentalMapper;
    }


    public RentalDTO rentCopy(int userId, int copyId, LocalDateTime dueDate) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        BookCopy copy = bookCopyDAO.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Book copy not found"));

        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new IllegalStateException("Copy is not available for rent");
        }

        copy.setStatus(CopyStatus.RENTED);
        bookCopyDAO.update(copy);

        Rental rental = Rental.builder()
                .user(user)
                .copy(copy)
                .startDate(LocalDateTime.now())
                .dueDate(dueDate)
                .status(RentalStatus.RENTED) // Исправлено на RENTED
                .build();

        return rentalMapper.toDto(rentalDAO.save(rental));
    }


    public RentalDTO returnCopy(int copyId) {
        BookCopy copy = bookCopyDAO.findById(copyId)
                .orElseThrow(() -> new EntityNotFoundException("Book copy not found"));

        Rental activeRental = rentalDAO.findActiveRentalByCopyId(copyId)
                .orElseThrow(() -> new IllegalStateException("No active rental for this copy"));

        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyDAO.update(copy);

        activeRental.setReturnDate(LocalDateTime.now());
        activeRental.setStatus(RentalStatus.RETURNED); // Исправлено на RETURNED

        return rentalMapper.toDto(rentalDAO.update(activeRental));
    }

    public List<RentalDTO> findOverdueRentals() {
        return rentalDAO.findOverdueRentals(LocalDateTime.now()).stream()
                .map(rental -> {
                    if (rental.getStatus() == RentalStatus.RENTED) {
                        rental.setStatus(RentalStatus.LATE);
                        rentalDAO.update(rental);
                    }
                    return rentalMapper.toDto(rental);
                })
                .toList();
    }

    public List<RentalDTO> getUserRentalHistory(int userId) {
        return rentalDAO.findByUserId(userId).stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    public List<RentalDTO> getCopyRentalHistory(int copyId) {
        return rentalDAO.findByCopyId(copyId).stream()
                .map(rentalMapper::toDto)
                .toList();
    }



}
