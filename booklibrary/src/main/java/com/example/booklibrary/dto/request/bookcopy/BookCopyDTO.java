package com.example.booklibrary.dto.request.bookcopy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class BookCopyDTO {
    private int copyId;
    private String status;
    private String bookTitle;
}
