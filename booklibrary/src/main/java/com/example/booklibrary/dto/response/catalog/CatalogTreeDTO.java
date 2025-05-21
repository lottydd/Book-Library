package com.example.booklibrary.dto.response.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "Дерево каталогов")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogTreeDTO  {

    @Schema(description = "ID каталога", example = "1")
    private Integer id;

    @Schema(description = "Название каталога", example = "Фантастика")
    private String name;

    @Schema(description = "Список дочерних каталогов")
    private List<CatalogTreeDTO> children;
}
