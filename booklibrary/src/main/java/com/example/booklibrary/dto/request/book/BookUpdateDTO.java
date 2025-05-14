package com.example.booklibrary.dto.request.book;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateDTO {
    @NotBlank(message = "Автор книги обязателен")
    private String author;

    @NotBlank(message = "Название книги обязательно")
    private String bookTitle;

    @NotBlank(message = "ISBN обязателен")
    private String isbn;

    @Min(value = 1000)
    @Max(value = 2025, message = "Год издания не может быть в будущем")
    private int publicationYear;

    @NotBlank(message = "Описание книги обязательно")
    private String description;
}