package com.example.booklibrary.dto.response.book;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private int id;
    private String author;
    private String bookTitle;
    private String isbn;
    private int publicationYear;
    private String description;
    private LocalDateTime storageArrivalDate;
}