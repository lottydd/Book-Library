package com.example.booklibrary.dto.request.bookcopy;

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
