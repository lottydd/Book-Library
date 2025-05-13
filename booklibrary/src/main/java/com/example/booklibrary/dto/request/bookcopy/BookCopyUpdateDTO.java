package com.example.booklibrary.dto.request.bookcopy;

import com.example.booklibrary.util.CopyStatus;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCopyUpdateDTO {


  private int copyId;

  private CopyStatus status;
}
