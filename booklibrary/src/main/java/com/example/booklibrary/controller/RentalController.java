package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.rental.RentalCopyDTO;
import com.example.booklibrary.dto.response.rental.RentalCopyStoryResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalLateResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalUserHistoryResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;


import com.example.booklibrary.dto.response.rental.RentalDTO;
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

    //+
    @PostMapping
    public ResponseEntity<RentalDTO> rentCopy(@RequestBody @Valid RentalCopyDTO rentalCopyDTO) {
        RentalDTO rentalDTO = rentalService.rentCopy(rentalCopyDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalDTO);
    }
    //нужно ли здесь проверку добавить в проверку того кто возвращает???  +
    @PutMapping("/{copyId}/return")
    public ResponseEntity<RentalDTO> returnBookCopy(@PathVariable Integer copyId) {
        RentalDTO rentalDTO = rentalService.returnCopy(new RequestIdDTO(copyId));
        return ResponseEntity.ok(rentalDTO);
    }
    // надо заполнить бд для прочека
    @GetMapping("/overdue")
    public ResponseEntity<List<RentalLateResponseDTO>> getOverdueRentals() {
        List<RentalLateResponseDTO> overdueRentals = rentalService.findOverdueRentals();
        return ResponseEntity.ok(overdueRentals);
    }
    // надо заполнить бд для прочека
    @PutMapping("/mark-overdue")
    public ResponseEntity<Void> markOverdueRentals() {
        rentalService.markOverdueRentalsAsLate();
        return ResponseEntity.noContent().build();
    }
    // +
    @GetMapping("/user-history/{userId}")
    public ResponseEntity<List<RentalUserHistoryResponseDTO>> getUserRentalHistory(
            @PathVariable int userId) {
        List<RentalUserHistoryResponseDTO> history = rentalService.getUserRentalHistory(userId);
        return ResponseEntity.ok(history);
    }
    // +
    @GetMapping("/copy-history/{copyId}")
    public ResponseEntity<List<RentalCopyStoryResponseDTO>> getCopyRentalHistory(
            @PathVariable int copyId) {
        List<RentalCopyStoryResponseDTO> history = rentalService.getCopyRentalHistory(copyId);
        return ResponseEntity.ok(history);
    }

}