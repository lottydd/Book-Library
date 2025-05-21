package com.example.booklibrary.dto.response.catalog;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Ответ после добавления книги в каталог")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogAddBookResponseDTO {

    @Schema(description = "ID каталога", example = "1")
    private Integer catalogId;

    @Schema(description = "Название каталога", example = "Фантастика")
    private String catalogName;

    @Schema(description = "ID книги", example = "10")
    private Integer bookId;

    @Schema(description = "Сообщение о результате", example = "Книга успешно добавлена в каталог")
    private String message;
}
