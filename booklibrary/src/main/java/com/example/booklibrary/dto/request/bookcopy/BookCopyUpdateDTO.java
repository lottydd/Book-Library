package com.example.booklibrary.dto.request.bookcopy;

import com.example.booklibrary.util.CopyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCopyUpdateDTO {

  @NotNull( message = "ID обновляемой копии не может быть равен нулю")
  private Integer copyId;

  @NotNull(message = "Статус обязателен")
  private CopyStatus status;
}
