package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.rental.RentalCopyDTO;
import com.example.booklibrary.dto.response.rental.RentalCopyStoryResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalLateResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalUserHistoryResponseDTO;
import com.example.booklibrary.security.CustomUserDetails;
import com.example.booklibrary.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<RentalDTO> rentCopy(@RequestBody @Valid RentalCopyDTO rentalCopyDTO) {
        RentalDTO rentalDTO = rentalService.rentCopy(rentalCopyDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalDTO);
    }
    //+
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/return/{copyId}")
    public ResponseEntity<RentalDTO> returnBookCopy(@PathVariable Integer copyId,
                                                    Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();

        RentalDTO rentalDTO = rentalService.returnCopy(userId, copyId);
        return ResponseEntity.ok(rentalDTO);
    }
    // +-
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue")
    public ResponseEntity<List<RentalLateResponseDTO>> getOverdueRentals() {
        List<RentalLateResponseDTO> overdueRentals = rentalService.findOverdueRentals();
        return ResponseEntity.ok(overdueRentals);
    }
    // +-
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/mark-overdue")
    public ResponseEntity<Void> markOverdueRentals() {
        rentalService.markOverdueRentalsAsLate();
        return ResponseEntity.noContent().build();
    }
    // +
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user-history/{userId}")
    public ResponseEntity<List<RentalUserHistoryResponseDTO>> getUserRentalHistory(
            @PathVariable int userId) {
        List<RentalUserHistoryResponseDTO> history = rentalService.getUserRentalHistory(userId);
        return ResponseEntity.ok(history);
    }
    // +
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/copy-history/{copyId}")
    public ResponseEntity<List<RentalCopyStoryResponseDTO>> getCopyRentalHistory(
            @PathVariable int copyId) {
        List<RentalCopyStoryResponseDTO> history = rentalService.getCopyRentalHistory(copyId);
        return ResponseEntity.ok(history);
    }

}