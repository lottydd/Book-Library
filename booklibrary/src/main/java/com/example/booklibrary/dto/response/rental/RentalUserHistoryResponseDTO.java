package com.example.booklibrary.dto.response.rental;

import lombok.*;

import java.time.LocalDateTime;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalUserHistoryResponseDTO {
    private Integer rentalId;
    private String bookTitle;
    private String bookAuthor;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;
}