package com.example.booklibrary.dto.request.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CatalogCreateDTO {

    @Nullable
    private Integer parentId;
    @NotBlank
    private String name;
}
