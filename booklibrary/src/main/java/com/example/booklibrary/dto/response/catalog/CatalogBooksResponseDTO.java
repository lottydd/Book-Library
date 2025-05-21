package com.example.booklibrary.dto.response.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Ответ: список книг в каталоге")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogBooksResponseDTO {

    @Schema(description = "Название книги", example = "Дюна")
    private String bookTitle;

    @Schema(description = "Автор книги", example = "Фрэнк Герберт")
    private String author;
}
