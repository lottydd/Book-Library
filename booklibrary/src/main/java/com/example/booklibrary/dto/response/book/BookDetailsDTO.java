package com.example.booklibrary.dto.response.book;


import lombok.*;

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