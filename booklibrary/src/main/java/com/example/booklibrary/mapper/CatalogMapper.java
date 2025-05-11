package com.example.booklibrary.mapper;

import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CatalogMapper {
    CatalogDTO toDto(Catalog catalog);

    default List<CatalogDTO> toDtoList(List<Catalog> catalogs) {
        return catalogs.stream()
                .map(this::toDto)
                .toList();
    }

    // Можно добавить методы для других преобразований
}