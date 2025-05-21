package com.example.booklibrary.dto.response.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Данные об аренде копии книги")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalDTO {
    @Schema(description = "ID аренды", example = "12")
    private int rentalId;

    @Schema(description = "ID пользователя", example = "1")
    private int userId;

    @Schema(description = "ID копии книги", example = "10")
    private int copyId;

    @Schema(description = "Название книги", example = "Война и мир")
    private String bookTitle;

    @Schema(description = "Дата начала аренды", example = "2025-05-20T12:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Дата возврата", example = "2025-06-20T12:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Фактическая дата возврата", example = "2025-06-18T10:00:00")
    private LocalDateTime returnDate;

    @Schema(description = "Статус аренды", example = "RETURNED")
    private String status;
}