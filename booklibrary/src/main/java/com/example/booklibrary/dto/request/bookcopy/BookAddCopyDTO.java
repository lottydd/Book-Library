package com.example.booklibrary.dto.request.bookcopy;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookAddCopyDTO {

    @NotBlank
    private int bookId;
    private int count;

}
