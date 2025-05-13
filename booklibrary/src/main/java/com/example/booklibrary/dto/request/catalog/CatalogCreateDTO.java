package com.example.booklibrary.dto.request.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogCreateDTO {

    @NotBlank
    private String name;

    @Nullable
    private Integer parentId;

}
