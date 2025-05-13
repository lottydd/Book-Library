package com.example.booklibrary.dto.response.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalDTO {
    private int rentalId;

    private int userId;

    private int copyId;

    private String bookTitle;

    private LocalDateTime startDate;

    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    private String status;
}