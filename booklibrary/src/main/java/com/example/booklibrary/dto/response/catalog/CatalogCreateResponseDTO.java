package com.example.booklibrary.dto.response.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Ответ создания каталога")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogCreateResponseDTO {

    @Schema(description = "ID нового каталога", example = "5")
    private Integer id;

    @Schema(description = "Название нового каталога", example = "Фантастика")
    private String name;

    @Schema(description = "ID родительского каталога", example = "2")
    private Integer parentId;

    @Schema(description = "Сообщение о результате операции", example = "Каталог успешно создан")
    private String message;
}