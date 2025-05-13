package com.example.booklibrary.dto.response.rental;

import lombok.*;

import java.time.LocalDateTime;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalCopyStoryResponseDTO {
    private Integer rentalId;
    private Integer userId;
    private String userUsername;
    private String status;
    private String bookTitle;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

}