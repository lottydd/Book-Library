package com.example.booklibrary.dto.request.bookcopy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Schema(description = "Запрос на добавление копий книги")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookAddCopyDTO {


    @Schema(description = "ID книги, для которой добавляются копии", example = "10", required = true)
    @NotNull( message = "ID обновляемой копии не может быть равен нулю")
    private Integer bookId;

    @Schema(description = "Количество копий для добавления", example = "5", required = true)
    @NotNull( message = "Количество добавляемых копий не может быть равно нулю")
    @Positive(message = "Количество добавляемых копий должно быть положительным")
    private Integer count;

}
