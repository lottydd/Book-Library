package com.example.booklibrary.dto.response.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "История аренды пользователя")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalUserHistoryResponseDTO {

    @Schema(description = "ID аренды", example = "1001")
    private Integer rentalId;

    @Schema(description = "Название книги", example = "Анна Каренина")
    private String bookTitle;

    @Schema(description = "Автор книги", example = "Лев Толстой")
    private String bookAuthor;

    @Schema(description = "Дата начала аренды", example = "2025-05-01T10:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Дата возврата", example = "2025-05-30T10:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Фактическая дата возврата", example = "2025-05-29T09:00:00")
    private LocalDateTime returnDate;

    @Schema(description = "Статус аренды", example = "RETURNED")
    private String status;
}