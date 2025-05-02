package com.example.booklibrary.dto.request.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class BookUpdateDTO {
    @NotBlank private String author;
    @NotBlank
    private String bookTitle;
    @Min(1000) private int publicationYear;
    private String description;
}
