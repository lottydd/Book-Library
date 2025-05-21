package com.example.booklibrary.dto.request.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Запрос на создание книги")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateDTO {

        @Schema(description = "Автор книги", example = "Фёдор Достоевский")
        @NotBlank(message = "Автор книги обязателен")
        private String author;

        @Schema(description = "Название книги", example = "Преступление и наказание")
        @NotBlank(message = "Название книги обязательно")
        private String bookTitle;

        @Schema(description = "ISBN книги")
        @NotBlank(message = "Некорректный формат ISBN ")
        private String isbn;

        @Schema(description = "Год издания", example = "1866")
        @Min(1000)
        @Max(value = 2025, message = "Год издания не может быть в будущем")
        private int publicationYear;

        @Schema(description = "Описание книги", example = "Роман о внутренней борьбе и раскаянии.")
        @NotBlank(message = "Описание книги обязательно")
        private String description;

        @Schema(description = "Количество копий", example = "5")
        @NotNull(message = "Количество копий обязательно")
        @Min(value = 1, message = "При добавлении книги должна быть минимум 1 копия")
        private Integer copiesCount;

    }

