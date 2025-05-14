package com.example.booklibrary.dto.response.catalog;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogTreeDTO  {
    private Integer id;
    private String name;
    private List<CatalogTreeDTO> children;
}
