package com.example.booklibrary.dto.request.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class CatalogAddBookDTO {
    @NotBlank
    private int catalogId;
    @NotBlank
    private int bookId;
}
