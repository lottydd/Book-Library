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
    private int rentalId;

    private int userId;

    private String bookTitle;

    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    private long daysLate;
}
