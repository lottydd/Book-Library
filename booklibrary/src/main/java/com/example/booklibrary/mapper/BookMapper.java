package com.example.booklibrary.mapper;


import com.example.booklibrary.dto.request.book.BookCreateDTO;
import com.example.booklibrary.dto.response.book.BookDetailsDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogBooksResponseDTO;
import com.example.booklibrary.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {BookCopyMapper.class, CatalogMapper.class})

public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "copies", ignore = true)
    @Mapping(target = "bookCatalogs", ignore = true)
    Book toEntity(BookCreateDTO dto);

    BookResponseDTO toResponseDTO(Book book);

    CatalogBooksResponseDTO toCatalogBookResponseDTO(Book book);

    BookDetailsDTO toDetailsDTO(Book book);

}
