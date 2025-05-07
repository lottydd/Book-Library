package com.example.booklibrary.mapper;

import com.example.booklibrary.model.BookCatalog;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CatalogMapper {
    default List<String> mapCatalogs(List<BookCatalog> catalogs) {
        return catalogs.stream()
                .map(bc -> bc.getCatalog().getName())
                .collect(Collectors.toList());
    }
}