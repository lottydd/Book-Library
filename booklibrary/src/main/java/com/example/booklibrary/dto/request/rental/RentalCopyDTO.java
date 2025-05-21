package com.example.booklibrary.dto.request.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Запрос для аренды копии книги")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalCopyDTO {

    @Schema(description = "ID пользователя", example = "1", required = true)
    @NotNull(message = "ID пользователя обязателен")
    @Positive(message = "ID пользователя должно быть положительным числом")
    private Integer userId;

    @Schema(description = "ID копии книги", example = "10", required = true)
    @NotNull(message = "ID копии книги обязателен")
    @Positive(message = "ID копии книги должно быть положительным числом")
    private Integer copyId;

    @Schema(description = "Срок возврата (дата в будущем)", example = "2025-06-30T23:59:59", required = true)
    @NotNull(message = "Дата возврата обязательна")
    @Future(message = "Дата возврата должна быть в будущем")
    private LocalDateTime dueDate;
}
