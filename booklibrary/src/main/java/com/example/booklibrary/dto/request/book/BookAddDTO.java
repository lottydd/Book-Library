package com.example.booklibrary.dto.request.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAddDTO {
    
        @NotBlank(message = "Author is required")
        private String author;

        @NotBlank(message = "Title is required")
        private String bookTitle;

        @NotBlank(message = "Invalid ISBN format")
        private String isbn;

        @Min(1000)
        private int publicationYear;
        @NotBlank
        private String description;

        @NotNull(message = "Copies count is required")
        @Min(value = 1, message = "At least 1 copy required")
        private Integer copiesCount;

        @NotNull(message = "Catalog IDs list cannot be null (use empty list for no catalogs)")
        private List<Integer> catalogIds = new ArrayList<>();;
    }

