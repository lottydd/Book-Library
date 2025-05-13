package com.example.booklibrary.mapper;


import com.example.booklibrary.dto.request.book.BookCreateDTO;
import com.example.booklibrary.dto.request.book.BookDetailsDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.dto.request.book.BookUpdateDTO;
import com.example.booklibrary.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = {BookCopyMapper.class, CatalogMapper.class})

public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "copies", ignore = true)
    @Mapping(target = "bookCatalogs", ignore = true)
    Book toEntity(BookCreateDTO dto);

    @Mapping(target = "availableCopies", ignore = true)
    @Mapping(target = "rentedCopies", ignore = true)
    @Mapping(target = "catalogs", ignore = true)
    BookResponseDTO toResponseDTO(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "copies", ignore = true)
    @Mapping(target = "bookCatalogs", ignore = true)
    @Mapping(target = "storageArrivalDate", ignore = true)
    BookDetailsDTO toDetailsDTO(Book book);

}
