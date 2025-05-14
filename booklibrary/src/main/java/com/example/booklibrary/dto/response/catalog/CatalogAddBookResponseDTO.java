package com.example.booklibrary.dto.response.catalog;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogAddBookResponseDTO {

    private Integer catalogId;
    private String catalogName;
    private Integer bookId;
    private String message;
}
