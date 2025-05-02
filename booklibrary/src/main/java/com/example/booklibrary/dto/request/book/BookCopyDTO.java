package com.example.booklibrary.dto.request.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class BookCopyDTO {
    private int copyId;
    private String status;
    private String bookTitle;
}
