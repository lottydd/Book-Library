package com.example.booklibrary.dto.request.bookcopy;

import com.example.booklibrary.util.CopyStatus;
import lombok.Data;

@Data
public class BookCopyUpdateDTO {


  private   int copyId;
  private CopyStatus status;
}
