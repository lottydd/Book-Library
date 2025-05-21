package com.example.booklibrary.dto.request.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Schema(description = "Запрос на создание каталога")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogCreateDTO {

    @Schema(description = "Название каталога", example = "Фантастика", required = true)
    @NotBlank(message = "Имя каталога не может быть пустым")
    private String name;

    @Schema(description = "ID родительского каталога (если есть)", example = "2")
    @Nullable
    @Positive(message = "ID копии должен быть положительным числом")
    private Integer parentId;

}
