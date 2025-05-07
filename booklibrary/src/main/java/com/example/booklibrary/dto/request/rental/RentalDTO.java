package com.example.booklibrary.dto.request.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    @Min(1)
    private int rentalId;

    @Min(1)
    private int userId;

    @Min(1)
    private int copyId;

    @NotBlank
    private String bookTitle;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    @NotBlank
    private String status;
}