package com.example.booklibrary.dto.response.book;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Ответ при создании или обновлении книги")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class BookResponseDTO {

    @Schema(description = "Уникальный ID книги", example = "101")
    private int id;

    @Schema(description = "Автор книги", example = "Иван Тургенев")
    private String author;

    @Schema(description = "Название книги", example = "Отцы и дети")
    private String bookTitle;

    @Schema(description = "ISBN книги", example = "978-5-389-14000-4")
    private String isbn;

    @Schema(description = "Год издания", example = "1862")
    private int publicationYear;

    @Schema(description = "Описание книги", example = "Роман о конфликте поколений и нигилизме.")
    private String description;

    @Schema(description = "Дата поступления книги в хранилище", example = "2025-05-21T10:15:30")
    private LocalDateTime storageArrivalDate;
}