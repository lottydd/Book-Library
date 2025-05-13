package com.example.booklibrary.dto.response.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDateTime;

import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalLateResponseDTO {
    @Min(1)
    private int rentalId;

    @Min(1)
    private int userId;

    @NotBlank
    private String bookTitle;

    @NotNull
    private LocalDateTime dueDate;

    @NotNull
    private LocalDateTime returnDate;

    private long daysLate;
}
