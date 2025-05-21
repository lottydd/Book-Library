package com.example.booklibrary.dto.request.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "Запрос на обновление информации о книге")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateDTO {


    @Schema(description = "Автор книги", example = "Лев Толстой")
    @NotBlank(message = "Автор книги обязателен")
    private String author;

    @Schema(description = "Название книги", example = "Война и мир")
    @NotBlank(message = "Название книги обязательно")
    private String bookTitle;

    @Schema(description = "ISBN книги", example = "978-5-17-118366-5")
    @NotBlank(message = "ISBN обязателен")
    private String isbn;

    @Schema(description = "Год издания", example = "1869")
    @Min(value = 1000)
    @Max(value = 2025, message = "Год издания не может быть в будущем")
    private int publicationYear;

    @Schema(description = "Описание книги", example = "Эпический роман о войне и человеческой судьбе.")
    @NotBlank(message = "Описание книги обязательно")
    private String description;
}