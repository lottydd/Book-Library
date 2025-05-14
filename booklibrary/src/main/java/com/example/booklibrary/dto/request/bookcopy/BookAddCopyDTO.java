package com.example.booklibrary.dto.request.bookcopy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookAddCopyDTO {

    @NotNull( message = "ID обновляемой копии не может быть равен нулю")
    private Integer bookId;

    @NotNull( message = "Количество добавляемых копий не может быть равно нулю")
    @Positive(message = "Количество добавляемых копий должно быть положительным")
    private Integer count;

}
