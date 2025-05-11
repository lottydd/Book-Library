package com.example.booklibrary.dto.request.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CatalogCreateDTO {

    @NotBlank
    private String name;

    @Nullable
    private Integer parentId;

}
