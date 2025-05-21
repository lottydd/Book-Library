package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.response.bookcopy.BookCopyDTO;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCopy;
import com.example.booklibrary.util.CopyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {

    @Mapping(target = "status", expression = "java(copy.getStatus().name())")
    @Mapping(target = "bookTitle", source = "book.bookTitle")
    BookCopyDTO toDto(BookCopy copy);

}