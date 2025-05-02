package com.example.booklibrary.dao;

import com.example.booklibrary.model.Rental;
import com.example.booklibrary.util.RentalStatus;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public class RentalDAO extends BaseDAO<Rental, Integer>{
    public RentalDAO() {
        super(Rental.class);
    }



    public Optional<Rental> findActiveRentalByCopyId(int copyId) {
        try {
            return Optional.ofNullable(entityManager.createQuery(
                            "SELECT r FROM Rental r WHERE " +
                                    "r.copy.copyId = :copyId AND " +
                                    "r.status = :status", Rental.class)
                    .setParameter("copyId", copyId)
                    .setParameter("status", RentalStatus.RENTED) // Исправлено на RENTED
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Rental> findOverdueRentals(LocalDateTime currentDate) {
        return entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE " +
                                "r.dueDate < :currentDate AND " +
                                "r.status = :status", Rental.class)
                .setParameter("currentDate", currentDate)
                .setParameter("status", RentalStatus.RENTED) // Ищем активные аренды
                .getResultList();
    }


    public List<Rental> findByUserId(int userId) {
        return entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE " +
                                "r.user.id = :userId", Rental.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Rental> findByCopyId(int copyId) {
        return entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE " +
                                "r.copy.copyId = :copyId", Rental.class)
                .setParameter("copyId", copyId)
                .getResultList();
    }
}
