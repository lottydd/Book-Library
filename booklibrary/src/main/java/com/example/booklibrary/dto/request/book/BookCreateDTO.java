package com.example.booklibrary.dto.request.book;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateDTO {
    
        @NotBlank(message = "Автор книги обязателен")
        private String author;

        @NotBlank(message = "Название книги обязательно")
        private String bookTitle;

        @NotBlank(message = "Некорректный формат ISBN ")
        private String isbn;

        @Min(1000)
        @Max(value = 2025, message = "Год издания не может быть в будущем")
        private int publicationYear;
        @NotBlank(message = "Описание книги обязательно")
        private String description;

        @NotNull(message = "Количество копий обязательно")
        @Min(value = 1, message = "При добавлении книги должна быть минимум 1 копия")
        private Integer copiesCount;

        @NotNull(message = "Список ID каталогов не может быть null (используйте пустой список, если книга еще не содержится в каталогах)")
        private List<Integer> catalogIds = new ArrayList<>();;
    }

