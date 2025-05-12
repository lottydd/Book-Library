package com.example.booklibrary.dto.request.rental;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RentalCopyDTO {
    private int userId;
    private int copyId;
    private LocalDateTime dueDate;
}
