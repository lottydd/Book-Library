package com.example.booklibrary.dto.response.book;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Детальная информация о книге")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class BookDetailsDTO {

    @Schema(description = "Автор книги", example = "Антон Чехов")
    private String author;

    @Schema(description = "Название книги", example = "Вишнёвый сад")
    private String bookTitle;

    @Schema(description = "Год издания", example = "1904")
    private int publicationYear;

    @Schema(description = "Описание книги", example = "Пьеса о переменах и разрушении традиционного уклада жизни.")
    private String description;
}