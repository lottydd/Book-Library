package com.example.booklibrary.dto.response.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalUserHistoryResponseDTO {
    private Integer rentalId;
    private String bookTitle;
    private String bookAuthor;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;
}