package com.example.booklibrary.dto.request.book;


import com.example.booklibrary.dto.response.book.BookResponseDTO;
import lombok.*;

import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailsDTO {
    private String author;
    private String bookTitle;
    private int publicationYear;
    private String description;
}