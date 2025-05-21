package com.example.booklibrary.dto.request.bookcopy;

import com.example.booklibrary.util.CopyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Schema(description = "Запрос на обновление статуса копии книги")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCopyUpdateDTO {


  @Schema(description = "ID копии книги", example = "123", required = true)
  @NotNull( message = "ID обновляемой копии не может быть равен нулю")
  private Integer copyId;

  @Schema(description = "Новый статус копии книги", example = "RENTED", required = true)
  @NotNull(message = "Статус обязателен")
  private CopyStatus status;
}
