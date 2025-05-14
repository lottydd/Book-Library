package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.RentalDAO;
import com.example.booklibrary.dao.UserDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.rental.RentalCopyDTO;
import com.example.booklibrary.dto.response.rental.RentalDTO;
import com.example.booklibrary.dto.response.rental.RentalCopyStoryResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalLateResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalUserHistoryResponseDTO;
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
import java.time.temporal.ChronoUnit;
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
    public RentalDTO rentCopy(RentalCopyDTO dto) {
        logger.debug("Попытка аренды копии книги. UserID: {}, CopyID: {}", dto.getUserId(), dto.getCopyId());
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден. UserID: {}", dto.getUserId());
                    return new EntityNotFoundException("User not found with id: " + dto.getUserId());
                });

        BookCopy copy = bookCopyDAO.findById(dto.getCopyId())
                .orElseThrow(() -> {
                    logger.warn("Копия книги не найдена.  CopyID:  {}", dto.getCopyId());
                    return new EntityNotFoundException("Book copy not found with id: " + dto.getCopyId());
                });

        validateRentTime(dto.getDueDate());
        validateCopyAvailableForRent(copy);
        copy.setStatus(CopyStatus.RENTED);
        bookCopyDAO.update(copy);
        logger.debug("Статус копии изменен на RENTED. CopyID: {}", dto.getCopyId());

        Rental rental = Rental.builder()
                .user(user)
                .copy(copy)
                .startDate(LocalDateTime.now())
                .dueDate(dto.getDueDate())
                .status(RentalStatus.RENTED)
                .build();

        Rental savedRental = rentalDAO.save(rental);
        logger.info("Аренда успешно создана. RentalID: {}, UserID: {}, CopyID: {}",
                savedRental.getId(), dto.getUserId(), dto.getCopyId());
        return rentalMapper.toDto(savedRental);
    }

    @Transactional
    public RentalDTO returnCopy(RequestIdDTO dto) {
        logger.debug("Попытка возврата копии книги. CopyID: {}", dto.getId());

        BookCopy copy = bookCopyDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.warn("Копия книги не найдена. CopyID: {}", dto.getId());
                    return new EntityNotFoundException("Book copy not found");
                });

        Rental activeRental = rentalDAO.findActiveRentalByCopyId(dto.getId())
                .orElseThrow(() -> {
                    logger.warn("Активная аренда не найдена для копии. CopyID: {}", dto.getId());
                    return new IllegalStateException("Нет активных аренд для этой копии");
                });

        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyDAO.update(copy);
        logger.debug("Статус копии изменен на AVAILABLE. CopyID: {}", dto.getId());

        activeRental.setReturnDate(LocalDateTime.now());
        activeRental.setStatus(RentalStatus.RETURNED);
        Rental updatedRental = rentalDAO.update(activeRental);

        logger.info("Копия успешно возвращена. RentalID: {}, CopyID: {}",
                updatedRental.getId(), dto.getId());
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional(readOnly = true)
    public List<RentalLateResponseDTO> findOverdueRentals() {
        logger.debug("Запрос просроченных аренд");
        List<Rental> overdueRentals = rentalDAO.findOverdueRentals(LocalDateTime.now());
        logger.info("Найдено {} просроченных аренд", overdueRentals.size());
        return overdueRentals.stream()
                .map(rentalMapper::toLateDto)
                .toList();
    }

    //Оставить как админский метод
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
    public List<RentalUserHistoryResponseDTO> getUserRentalHistory(int userId) {
        logger.debug("Запрос истории аренд пользователя. UserID: {}", userId);
        List<Rental> rentals = rentalDAO.findByUserId(userId);
        logger.info("Найдено {} аренд для пользователя UserID: {}", rentals.size(), userId);
        return rentalMapper.toUserHistoryDtoList(rentals);
    }

    @Transactional(readOnly = true)
    public List<RentalCopyStoryResponseDTO> getCopyRentalHistory(int copyId) {
        logger.debug("Запрос истории аренд копии книги. CopyID: {}", copyId);
        List<Rental> rentals = rentalDAO.findByCopyId(copyId);
        logger.info("Найдено {} аренд для копии CopyID: {}", rentals.size(), copyId);
        return rentalMapper.toCopyStoryDtoList(rentals);
    }

    private void validateCopyAvailableForRent(BookCopy copy) {
        logger.debug("Проверка доступности копии для аренды. CopyID: {}", copy.getCopyId());
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            logger.warn("Копия недоступна для аренды. CopyID: {}, Status: {}",
                    copy.getCopyId(), copy.getStatus());
            throw new IllegalStateException(
                    "Копия с ID " + copy.getCopyId() + " недоступна для аренды. Текущий статус: " + copy.getStatus()
            );
        }
    }

    private void validateRentTime(LocalDateTime dueTime) {
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(now, dueTime);

        if (days < 7) {
            throw new IllegalArgumentException("Минимальный срок аренды - 1 неделя");
        }
        if (days > 365) {
            throw new IllegalArgumentException("Максимальный срок аренды - 1 год");
        }
    }
}
