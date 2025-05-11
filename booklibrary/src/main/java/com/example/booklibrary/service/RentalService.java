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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

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

    @Transactional
    public RentalDTO rentCopy(int userId, int copyId, LocalDateTime dueDate) {
        logger.debug("Попытка аренды копии книги. UserID: {}, CopyID: {}", userId, copyId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });

        BookCopy copy = bookCopyDAO.findById(copyId)
                .orElseThrow(() -> {
                    logger.warn("Копия книги не найдена. CopyID:  {}", copyId);
                    return new EntityNotFoundException("Book copy not found with id: " + copyId);
                });

        validateCopyAvailableForRent(copy);
        copy.setStatus(CopyStatus.RENTED);
        bookCopyDAO.update(copy);
        logger.debug("Статус копии изменен на RENTED. CopyID: {}", copyId);

        Rental rental = Rental.builder()
                .user(user)
                .copy(copy)
                .startDate(LocalDateTime.now())
                .dueDate(dueDate)
                .status(RentalStatus.RENTED)
                .build();

        Rental savedRental = rentalDAO.save(rental);
        logger.info("Аренда успешно создана. RentalID: {}, UserID: {}, CopyID: {}",
                savedRental.getId(), userId, copyId);

        return rentalMapper.toDto(savedRental);
    }

    @Transactional
    public RentalDTO returnCopy(int copyId) {
        logger.debug("Попытка возврата копии книги. CopyID: {}", copyId);

        BookCopy copy = bookCopyDAO.findById(copyId)
                .orElseThrow(() -> {
                    logger.warn("Копия книги не найдена. CopyID: {}", copyId);
                    return new EntityNotFoundException("Book copy not found");
                });

        Rental activeRental = rentalDAO.findActiveRentalByCopyId(copyId)
                .orElseThrow(() -> {
                    logger.warn("Активная аренда не найдена для копии. CopyID: {}", copyId);
                    return new IllegalStateException("No active rental for this copy");
                });

        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyDAO.update(copy);
        logger.debug("Статус копии изменен на AVAILABLE. CopyID: {}", copyId);

        activeRental.setReturnDate(LocalDateTime.now());
        activeRental.setStatus(RentalStatus.RETURNED);
        Rental updatedRental = rentalDAO.update(activeRental);

        logger.info("Копия успешно возвращена. RentalID: {}, CopyID: {}",
                updatedRental.getId(), copyId);

        return rentalMapper.toDto(updatedRental);
    }

    @Transactional(readOnly = true)
    public List<RentalDTO> findOverdueRentals() {
        logger.debug("Запрос просроченных аренд");
        List<Rental> overdueRentals = rentalDAO.findOverdueRentals(LocalDateTime.now());
        logger.info("Найдено {} просроченных аренд", overdueRentals.size());
        return overdueRentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Transactional
    public void markOverdueRentalsAsLate() {
        logger.debug("Пометка просроченных аренд как LATE");
        List<Rental> overdueRentals = rentalDAO.findOverdueRentals(LocalDateTime.now());
        overdueRentals.forEach(rental -> {
            rental.setStatus(RentalStatus.LATE);
            rentalDAO.update(rental);
            logger.debug("Аренда помечена как LATE. RentalID: {}", rental.getId());
        });
        logger.info("Помечено {} аренд как LATE", overdueRentals.size());
    }

    @Transactional(readOnly = true)

    public List<RentalDTO> getUserRentalHistory(int userId) {
        logger.debug("Запрос истории аренд пользователя. UserID: {}", userId);
        List<Rental> rentals = rentalDAO.findByUserId(userId);
        logger.info("Найдено {} аренд для пользователя UserID: {}", rentals.size(), userId);
        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RentalDTO> getCopyRentalHistory(int copyId) {
        logger.debug("Запрос истории аренд копии книги. CopyID: {}", copyId);
        List<Rental> rentals = rentalDAO.findByCopyId(copyId);
        logger.info("Найдено {} аренд для копии CopyID: {}", rentals.size(), copyId);
        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }
    private void validateCopyAvailableForRent(BookCopy copy) {
        logger.debug("Проверка доступности копии для аренды. CopyID: {}", copy.getCopyId());
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            logger.warn("Копия недоступна для аренды. CopyID: {}, Status: {}",
                    copy.getCopyId(), copy.getStatus());
            throw new IllegalStateException(
                    "Copy with id " + copy.getCopyId() + " is not available. Current status: " + copy.getStatus()
            );
        }
    }



}
