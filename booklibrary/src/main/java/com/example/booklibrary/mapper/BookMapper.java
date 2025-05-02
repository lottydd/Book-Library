package com.example.booklibrary.mapper;


import com.example.booklibrary.dto.request.book.BookAddDTO;
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
    Book toEntity(BookAddDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "copies", ignore = true)
    @Mapping(target = "bookCatalogs", ignore = true)
    @Mapping(target = "storageArrivalDate", ignore = true)
    void updateFromDto(BookUpdateDTO dto, @MappingTarget Book book);

    @Mapping(target = "availableCopies", ignore = true)
    @Mapping(target = "rentedCopies", ignore = true)
    @Mapping(target = "catalogs", ignore = true)
    BookResponseDTO toResponseDTO(Book book);

    @Mapping(source = "copies", target = "availableCopies", qualifiedByName = "countAvailable")
    @Mapping(source = "copies", target = "rentedCopies", qualifiedByName = "countRented")
    @Mapping(source = "bookCatalogs", target = "catalogs", qualifiedByName = "mapCatalogs")
    BookDetailsDTO toDetailsDTO(Book book);

}
