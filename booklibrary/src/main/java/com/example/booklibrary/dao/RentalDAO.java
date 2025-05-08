package com.example.booklibrary.dao;

import com.example.booklibrary.model.Rental;
import com.example.booklibrary.util.RentalStatus;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public class RentalDAO extends BaseDAO<Rental, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(RentalDAO.class);

    public RentalDAO() {
        super(Rental.class);
    }

    public Optional<Rental> findActiveRentalByCopyId(int copyId) {
        logger.debug("Поиск активной аренды для копии книги с ID: {}", copyId);

        try {
            Optional<Rental> result = entityManager.createQuery(
                            "SELECT r FROM Rental r WHERE r.copy.id = :copyId " +
                                    "AND r.status IN :activeStatuses", Rental.class)
                    .setParameter("copyId", copyId)
                    .setParameter("activeStatuses", List.of(RentalStatus.RENTED, RentalStatus.LATE))
                    .getResultStream()
                    .findFirst();

            if (result.isPresent()) {
                Rental rental = result.get();
                logger.info("Найдена активная аренда для копии ID: {}. " +
                                "Статус: {}, ID аренды: {}, Пользователь: {}",
                        copyId,
                        rental.getStatus(),
                        rental.getId(),
                        rental.getUser() != null ? rental.getUser().getId() : "null");
            }
            return result;
        } catch (NoResultException e) {
            logger.debug("Активная аренда не найдена для копии ID: {}", copyId);
            return Optional.empty();
        }
    }

    public List<Rental> findOverdueRentals(LocalDateTime currentDate) {
        logger.debug("Поиск просроченных аренд");
        List<Rental> result = entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE " +
                                "r.dueDate < :currentDate AND " +
                                "r.status = :status", Rental.class)
                .setParameter("currentDate", currentDate)
                .setParameter("status", RentalStatus.RENTED)
                .getResultList();
        logger.info("Найдено просроченных аренд: {}", result.size());
        return result;
    }


    public List<Rental> findByUserId(int userId) {
        return entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE " +
                                "r.user.id = :userId", Rental.class)  //Вснуть сюда сортировку либо  отсортировать уже в методе сервиса
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Rental> findByCopyId(int copyId) {
        return entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE " +
                                "r.copy.copyId = :copyId", Rental.class)  //Вснуть сюда сортировку либо  отсортировать уже в методе сервиса
                .setParameter("copyId", copyId)
                .getResultList();
    }
}
