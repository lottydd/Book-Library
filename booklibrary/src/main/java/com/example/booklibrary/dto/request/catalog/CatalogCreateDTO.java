package com.example.booklibrary.dto.request.catalog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogCreateDTO {

    @NotBlank(message = "Имя каталога не может быть пустым")
    private String name;

    @NotNull(message = "ID копии обязательно")
    @Positive(message = "ID копии должен быть положительным числом")
    private Integer parentId;

}
