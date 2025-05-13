package com.example.booklibrary.dto.response.catalog;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogCreateResponseDTO {
    private Integer id;
    private String name;
    private Integer parentId;
    private String message;
}