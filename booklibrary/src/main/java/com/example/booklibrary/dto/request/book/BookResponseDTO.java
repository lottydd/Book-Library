package com.example.booklibrary.dto.request.book;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookResponseDTO {
    private int id;
    private String author;
    private String bookTitle;
    private String isbn;
    private int publicationYear;
    private String description;
    private LocalDateTime storageArrivalDate;
}