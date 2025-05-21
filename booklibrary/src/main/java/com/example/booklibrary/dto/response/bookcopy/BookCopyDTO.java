package com.example.booklibrary.dto.response.bookcopy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "Ответ с информацией о копии книги")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class BookCopyDTO {
    @Schema(description = "ID копии книги", example = "123")
    private int copyId;

    @Schema(description = "Статус копии книги", example = "AVAILABLE")
    private String status;

    @Schema(description = "Название книги", example = "Война и мир")
    private String bookTitle;
}
