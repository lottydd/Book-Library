package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.response.bookcopy.BookCopyDTO;
import com.example.booklibrary.model.BookCopy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopyDTO toDto(BookCopy copy);



}
