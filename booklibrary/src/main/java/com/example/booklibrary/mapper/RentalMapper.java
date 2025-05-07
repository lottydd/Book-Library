package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.request.rental.RentalDTO;
import com.example.booklibrary.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "copyId", source = "copy.copyId")
    @Mapping(target = "bookTitle", source = "copy.book.bookTitle")
    @Mapping(target = "status", expression = "java(rental.getStatus().name())")
    RentalDTO toDto(Rental rental);
}
