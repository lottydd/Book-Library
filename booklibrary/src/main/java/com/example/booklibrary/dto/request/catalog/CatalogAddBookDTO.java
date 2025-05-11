package com.example.booklibrary.dto.request.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter

public class CatalogAddBookDTO {
    @NotBlank
    private int catalogId;
    @NotBlank
    private int bookId;
}
