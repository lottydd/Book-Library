package com.example.booklibrary.dto.request.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateDTO {
    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Title is required")
    private String bookTitle;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @Min(value = 1000, message = "Publication year must be at least 1000")
    private int publicationYear;

    @NotBlank(message = "Desc is required")
    private String description;
}