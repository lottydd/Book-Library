package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogTreeDTO;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    @Mapping(target = "catalogId", source = "catalog.id")
    @Mapping(target = "catalogName", source = "catalog.name")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "message", expression = "java(\"Book successfully added to catalog\")")
    CatalogAddBookResponseDTO toCatalogAddBookResponseDTO(BookCatalog bookCatalog);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "message", constant = "Catalog created successfully")
    CatalogCreateResponseDTO toCatalogCreateResponseDTO(Catalog catalog);

    @Mapping(target = "children", source = "children")
    CatalogTreeDTO toTreeDto(Catalog catalog);

    default List<CatalogTreeDTO> mapChildren(List<Catalog> children) {
        if (children == null) return Collections.emptyList();
        return children.stream()
                .map(this::toTreeDto)
                .toList();
    }

    default List<CatalogTreeDTO> toTreeDtoList(List<Catalog> catalogs) {
        return catalogs.stream()
                .map(this::toTreeDto)
                .collect(Collectors.toList());
    }

    @Mapping(target = "name", source = "name")
    @Mapping(target = "parent", ignore = true)
    Catalog toEntity(CatalogCreateDTO dto);
}