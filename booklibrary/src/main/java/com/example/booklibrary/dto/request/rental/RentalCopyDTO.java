package com.example.booklibrary.dto.request.rental;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalCopyDTO {


    private int userId;

    private int copyId;

    private LocalDateTime dueDate;
}
