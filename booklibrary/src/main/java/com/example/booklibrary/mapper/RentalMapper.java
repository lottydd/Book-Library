package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.response.rental.RentalCopyStoryResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalDTO;
import com.example.booklibrary.dto.response.rental.RentalLateResponseDTO;
import com.example.booklibrary.dto.response.rental.RentalUserHistoryResponseDTO;
import com.example.booklibrary.model.Rental;
import com.example.booklibrary.util.RentalStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "copyId", source = "copy.copyId")
    @Mapping(target = "rentalId", source = "id")
    @Mapping(target = "bookTitle", source = "copy.book.bookTitle")
    @Mapping(target = "status", expression = "java(rental.getStatus().name())")
    RentalDTO toDto(Rental rental);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookTitle", source = "copy.book.bookTitle")
    @Mapping(target = "daysLate", source = "dueDate", qualifiedByName = "calculateDaysLate")
    @Mapping(target = "rentalId", source = "id")
    RentalLateResponseDTO toLateDto(Rental rental);

    @Mapping(target = "rentalId", source = "id")
    @Mapping(target = "bookTitle", source = "copy.book.bookTitle")
    @Mapping(target = "bookAuthor", source = "copy.book.author")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusToString")
    RentalUserHistoryResponseDTO toUserHistoryDto(Rental rental);

    @Mapping(target = "rentalId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userUsername", source = "user.username")
    @Mapping(target = "status", expression = "java(rental.getStatus().name())")
    @Mapping(target = "bookTitle", source = "copy.book.bookTitle")
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

    @Named("mapStatusToString")
    default String mapStatusToString(RentalStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("calculateDaysLate")
    default long calculateDaysLate(LocalDateTime dueDate) {
        if (dueDate == null || dueDate.isAfter(LocalDateTime.now())) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDateTime.now());
    }

}