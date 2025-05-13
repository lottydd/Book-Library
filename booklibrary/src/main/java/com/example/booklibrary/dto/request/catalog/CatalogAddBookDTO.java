package com.example.booklibrary.dto.request.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class CatalogAddBookDTO {

    @NotNull(message = "ID каталога не может быть пустым")
    private int catalogId;
    @NotNull(message = "ID книги не может быть пустым")
    private int bookId;
}
