package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.response.rental.RentalCopyStoryResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalDTO;
import com.example.booklibrary.dto.response.rental.RentalLateResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalUserHistoryResponseDTO;
import com.example.booklibrary.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "copyId", source = "copy.copyId")
    @Mapping(target = "bookTitle", source = "copy.book.bookTitle")
    @Mapping(target = "status", expression = "java(rental.getStatus().name())")
    RentalDTO toDto(Rental rental);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookTitle", source = "copy.book.bookTitle")
    @Mapping(target = "daysLate", source = "dueDate", qualifiedByName = "calculateDaysLate")
    RentalLateResponseDTO toLateDto(Rental rental);

    @Mapping(target = "rentalId", source = "id")
    @Mapping(target = "bookTitle", source = "copy.book.title")
    @Mapping(target = "bookAuthor", source = "copy.book.author.name")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusToString")
    RentalUserHistoryResponseDTO toUserHistoryDto(Rental rental);

    @Mapping(target = "rentalId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userUsername", source = "user.username")
    @Mapping(target = "status", expression = "java(rental.getStatus().name())")
    @Mapping(target = "bookTitle", source = "copy.book.title")
    RentalCopyStoryResponseDTO toCopyStoryDto(Rental rental);

    default List<RentalCopyStoryResponseDTO> toCopyStoryDtoList(List<Rental> rentals) {
        if (rentals == null) {
            return Collections.emptyList();
        }
        return rentals.stream()
                .map(this::toCopyStoryDto)
                .toList();
    }


    default List<RentalUserHistoryResponseDTO> toUserHistoryDtoList(List<Rental> rentals) {
        if (rentals == null) {
            return Collections.emptyList();
        }
        return rentals.stream()
                .map(this::toUserHistoryDto)
                .toList();
    }


}