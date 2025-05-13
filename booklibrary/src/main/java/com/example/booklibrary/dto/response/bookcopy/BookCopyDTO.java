package com.example.booklibrary.dto.response.bookcopy;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class BookCopyDTO {
    private int copyId;
    private String status;
    private String bookTitle;
}
