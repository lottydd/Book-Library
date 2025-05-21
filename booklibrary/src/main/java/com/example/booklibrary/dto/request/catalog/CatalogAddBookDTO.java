package com.example.booklibrary.dto.request.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "Запрос на добавление книги в каталог")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class CatalogAddBookDTO {

    @Schema(description = "ID каталога", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID каталога не может быть пустым")
    private int catalogId;

    @Schema(description = "ID книги", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID книги не может быть пустым")
    private int bookId;
}
