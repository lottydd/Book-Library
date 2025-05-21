package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCopyDAO;
import com.example.booklibrary.dao.RentalDAO;
import com.example.booklibrary.dao.UserDAO;
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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public RentalService(RentalDAO rentalDAO, BookCopyDAO bookCopyDAO, UserDAO userDAO, RentalMapper rentalMapper) {
        this.rentalDAO = rentalDAO;
        this.bookCopyDAO = bookCopyDAO;
        this.userDAO = userDAO;
        this.rentalMapper = rentalMapper;
    }

    @Transactional
    public RentalDTO rentCopy(RentalCopyDTO dto) {
        logger.info("Попытка аренды копии книги. UserID: {}, CopyID: {}", dto.getUserId(), dto.getCopyId());
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", dto.getUserId());
                    return new EntityNotFoundException("User not found with id: " + dto.getUserId());
                });

        BookCopy copy = bookCopyDAO.findById(dto.getCopyId())
                .orElseThrow(() -> {
                    logger.error("Копия книги не найдена.  CopyID:  {}", dto.getCopyId());
                    return new EntityNotFoundException("Book copy not found with id: " + dto.getCopyId());
                });

        validateRentTime(dto.getDueDate());
        validateCopyAvailableForRent(copy);
        copy.setStatus(CopyStatus.RENTED);
        bookCopyDAO.update(copy);
        logger.info("Статус копии изменен на RENTED. CopyID: {}", dto.getCopyId());

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
    public RentalDTO returnCopy(int userId, int copyId) {
        logger.info("Попытка возврата копии книги. CopyID: {}", copyId);

        BookCopy copy = bookCopyDAO.findById(copyId)
                .orElseThrow(() -> {
                    logger.error("Копия книги не найдена. CopyID: {}", copyId);
                    return new EntityNotFoundException("Book copy not found");
                });

        Rental activeRental = rentalDAO.findActiveRentalByCopyId(copyId)
                .orElseThrow(() -> {
                    logger.error("Активная аренда не найдена для копии. CopyID: {}", copyId);
                    return new IllegalStateException("Нет активных аренд для этой копии");
                });
        if (activeRental.getUser().getId() != userId) {
            logger.error("Попытка возврата копии другим пользователем. UserID: {}, RentalUserID: {}",
                    userId, activeRental.getUser().getId());
            throw new SecurityException("Вы не можете вернуть копию, арендованную другим пользователем");
        }


        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyDAO.update(copy);
        logger.info("Статус копии изменен на AVAILABLE. CopyID: {}", copyId);

        activeRental.setReturnDate(LocalDateTime.now());
        activeRental.setStatus(RentalStatus.RETURNED);
        Rental updatedRental = rentalDAO.update(activeRental);

        logger.info("Копия успешно возвращена. RentalID: {}, CopyID: {}",
                updatedRental.getId(), copyId);
        return rentalMapper.toDto(updatedRental);
    }

    @Transactional(readOnly = true)
    public List<RentalLateResponseDTO> findOverdueRentals() {
        logger.info("Запрос просроченных аренд");
        List<Rental> overdueRentals = rentalDAO.findOverdueRentals(LocalDateTime.now());
        logger.info("Найдено {} просроченных аренд", overdueRentals.size());
        return overdueRentals.stream()
                .map(rentalMapper::toLateDto)
                .toList();
    }

    @Transactional
    public void markOverdueRentalsAsLate() {
        logger.info("Пометка просроченных аренд как LATE");
        List<Rental> overdueRentals = rentalDAO.findOverdueRentals(LocalDateTime.now());
        overdueRentals.forEach(rental -> {
            rental.setStatus(RentalStatus.LATE);
            rentalDAO.update(rental);
            logger.info("Аренда помечена как LATE. RentalID: {}", rental.getId());
        });
        logger.info("Помечено {} аренд как LATE", overdueRentals.size());
    }

    @Transactional(readOnly = true)
    public List<RentalUserHistoryResponseDTO> getUserRentalHistory(int userId) {
        logger.info("Запрос истории аренд пользователя. UserID: {}", userId);
        List<Rental> rentals = rentalDAO.findByUserId(userId);
        logger.info("Найдено {} аренд для пользователя UserID: {}", rentals.size(), userId);
        return rentalMapper.toUserHistoryDtoList(rentals);
    }

    @Transactional(readOnly = true)
    public List<RentalCopyStoryResponseDTO> getCopyRentalHistory(int copyId) {
        logger.info("Запрос истории аренд копии книги. CopyID: {}", copyId);
        List<Rental> rentals = rentalDAO.findByCopyId(copyId);
        logger.info("Найдено {} аренд для копии CopyID: {}", rentals.size(), copyId);
        return rentalMapper.toCopyStoryDtoList(rentals);
    }

    private void validateCopyAvailableForRent(BookCopy copy) {
        logger.info("Проверка доступности копии для аренды. CopyID: {}", copy.getCopyId());
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            logger.error("Копия недоступна для аренды. CopyID: {}, Status: {}",
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
