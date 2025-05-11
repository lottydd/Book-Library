package com.example.booklibrary.dto.request.book;


import com.example.booklibrary.dto.response.book.BookResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class BookDetailsDTO {
    private String author;
    private String bookTitle;
    private int publicationYear;
    private String description;
}