package com.example.booklibrary.dto.request.book;


import com.example.booklibrary.dto.response.book.BookResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookDetailsDTO extends BookResponseDTO {
    private long availableCopies;
    private long rentedCopies;
    private List<String> catalogs;
}
