package com.example.booklibrary.controller;

import org.springframework.web.bind.annotation.RestController;


import com.example.booklibrary.dto.request.rental.RentalDTO;
import com.example.booklibrary.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<RentalDTO> rentBook(
            @RequestParam int userId,
            @RequestParam int copyId,
            @RequestParam LocalDateTime dueDate) {
        return new ResponseEntity<>(
                rentalService.rentCopy(userId, copyId, dueDate),
                HttpStatus.CREATED);
    }

    @PostMapping("/{copyId}/return")
    public ResponseEntity<RentalDTO> returnBook(@PathVariable int copyId) {
        return ResponseEntity.ok(rentalService.returnCopy(copyId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<RentalDTO>> getOverdueRentals() {
        return ResponseEntity.ok(rentalService.findOverdueRentals());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<RentalDTO>> getUserRentals(@PathVariable int userId) {
        return ResponseEntity.ok(rentalService.getUserRentalHistory(userId));
    }
}