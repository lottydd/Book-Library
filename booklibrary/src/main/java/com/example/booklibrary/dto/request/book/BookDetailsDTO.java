package com.example.booklibrary.dto.request.book;


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
