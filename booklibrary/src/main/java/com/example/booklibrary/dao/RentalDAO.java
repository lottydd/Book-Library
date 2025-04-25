package com.example.booklibrary.dao;

import com.example.booklibrary.model.Rental;

public class RentalDAO extends BaseDAO<Rental, Integer>{
    public RentalDAO() {
        super(Rental.class);
    }
}
