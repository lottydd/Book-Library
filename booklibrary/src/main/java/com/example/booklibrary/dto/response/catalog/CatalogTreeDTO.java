package com.example.booklibrary.dto.response.catalog;

import lombok.Data;
import java.util.List;

@Data
public class CatalogTreeDTO  {
    private Integer id;
    private String name;
    private List<CatalogTreeDTO> children;
}
