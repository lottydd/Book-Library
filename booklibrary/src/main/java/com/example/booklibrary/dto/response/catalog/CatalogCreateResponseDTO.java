package com.example.booklibrary.dto.response.catalog;

import lombok.Data;

@Data
public class CatalogCreateResponseDTO {
    private Integer id;
    private String name;
    private Integer parentId;
    private String message;
}