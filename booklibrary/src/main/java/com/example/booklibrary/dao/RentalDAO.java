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
        return entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE r.copy.id = :copyId AND r.status IN :activeStatuses", Rental.class)
                .setParameter("copyId", copyId)
                .setParameter("activeStatuses", List.of(RentalStatus.RENTED, RentalStatus.LATE))
                .getResultStream()
                .findFirst();
    }

    public List<Rental> findOverdueRentals(LocalDateTime currentDate) {
        return entityManager.createQuery(
                        "SELECT r FROM Rental r WHERE " +
                                "r.dueDate < :currentDate AND " +
                                "r.status = :status", Rental.class)
                .setParameter("currentDate", currentDate)
                .setParameter("status", RentalStatus.RENTED)
                .getResultList();
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
