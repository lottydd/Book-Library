package com.example.booklibrary.dto.request.rental;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalCopyDTO {

    @NotNull(message = "ID пользователя обязателен")
    @Positive(message = "ID пользователя должно быть положительным числом")
    private Integer userId;

    @NotNull(message = "ID копии книги обязателен")
    @Positive(message = "ID копии книги должно быть положительным числом")
    private Integer copyId;

    @NotNull(message = "Дата возврата обязательна")
    @Future(message = "Дата возврата должна быть в будущем")
    private LocalDateTime dueDate;
}
