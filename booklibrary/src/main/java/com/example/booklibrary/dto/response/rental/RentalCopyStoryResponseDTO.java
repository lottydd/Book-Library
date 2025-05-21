package com.example.booklibrary.dto.response.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;


@Schema(description = "История аренды конкретной копии книги")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalCopyStoryResponseDTO {



    @Schema(description = "ID аренды", example = "10")
    private Integer rentalId;

    @Schema(description = "ID пользователя", example = "1")
    private Integer userId;

    @Schema(description = "Имя пользователя", example = "johndoe")
    private String userUsername;

    @Schema(description = "Статус аренды", example = "LATE")
    private String status;

    @Schema(description = "Название книги", example = "Идиот")
    private String bookTitle;

    @Schema(description = "Дата начала аренды", example = "2025-04-01T09:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Дата возврата", example = "2025-04-30T09:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Фактическая дата возврата", example = "2025-05-01T09:00:00")
    private LocalDateTime returnDate;

}