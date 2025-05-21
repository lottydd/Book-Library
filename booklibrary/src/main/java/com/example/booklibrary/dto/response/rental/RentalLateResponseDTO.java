package com.example.booklibrary.dto.response.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDateTime;

import lombok.*;

@Schema(description = "Информация о просроченной аренде")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalLateResponseDTO {

    @Schema(description = "ID аренды", example = "10")
    private int rentalId;

    @Schema(description = "ID пользователя", example = "1")
    private int userId;

    @Schema(description = "Название книги", example = "Преступление и наказание")
    private String bookTitle;

    @Schema(description = "Дата возврата", example = "2025-06-01T12:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Фактическая дата возврата", example = "2025-06-05T14:00:00")
    private LocalDateTime returnDate;

    @Schema(description = "Количество дней просрочки", example = "4")
    private long daysLate;
}
