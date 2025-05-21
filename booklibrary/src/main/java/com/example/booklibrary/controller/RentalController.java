package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.rental.RentalCopyDTO;
import com.example.booklibrary.dto.response.rental.RentalCopyStoryResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalLateResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalUserHistoryResponseDTO;
import com.example.booklibrary.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;


import com.example.booklibrary.dto.response.rental.RentalDTO;
import com.example.booklibrary.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rental", description = "управление арендами книг")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Аренда копии книги", description = "Позволяет пользователю арендовать копию книги")

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<RentalDTO> rentCopy(@RequestBody @Valid RentalCopyDTO rentalCopyDTO) {
        RentalDTO rentalDTO = rentalService.rentCopy(rentalCopyDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalDTO);
    }


    @Operation(summary = "Возврат копии книги", description = "Позволяет пользователю вернуть ранее арендованную копию книги")

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/return/{copyId}")
    public ResponseEntity<RentalDTO> returnBookCopy
            (@Parameter(description = "ID копии книги", example = "10") @PathVariable Integer copyId,
             Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();
        RentalDTO rentalDTO = rentalService.returnCopy(userId, copyId);
        return ResponseEntity.ok(rentalDTO);
    }

    @Operation(summary = "Получение просроченных аренд", description = "Возвращает список всех просроченных аренд (только для ADMIN)")

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue")
    public ResponseEntity<List<RentalLateResponseDTO>> getOverdueRentals() {
        List<RentalLateResponseDTO> overdueRentals = rentalService.findOverdueRentals();
        return ResponseEntity.ok(overdueRentals);
    }

    @Operation(summary = "Отметить аренды как просроченные", description = "Помечает все просроченные аренды как 'LATE'. Только для администратора")

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/mark-overdue")
    public ResponseEntity<Void> markOverdueRentals() {
        rentalService.markOverdueRentalsAsLate();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "История аренды пользователя", description = "Возвращает историю аренды по ID пользователя. Только для администратора")

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user-history/{userId}")
    public ResponseEntity<List<RentalUserHistoryResponseDTO>> getUserRentalHistory(
            @Parameter(description = "ID пользователя", example = "5") @PathVariable int userId) {
        List<RentalUserHistoryResponseDTO> history = rentalService.getUserRentalHistory(userId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "История аренды копии книги", description = "Возвращает историю аренды по ID копии книги . Только для администратора")

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/copy-history/{copyId}")
    public ResponseEntity<List<RentalCopyStoryResponseDTO>> getCopyRentalHistory(
            @Parameter(description = "ID копии книги", example = "12") @PathVariable int copyId) {
        List<RentalCopyStoryResponseDTO> history = rentalService.getCopyRentalHistory(copyId);
        return ResponseEntity.ok(history);
    }

}