package com.example.booklibrary.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogBooksResponseDTO {

    private String bookTitle;
    private String author;

}
