package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.book.BookUpdateDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogTreeDTO;
import com.example.booklibrary.model.Catalog;
import com.example.booklibrary.service.CatalogService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    //+
    @PostMapping
    public ResponseEntity<CatalogCreateResponseDTO> createCatalog
            (@RequestBody @Valid CatalogCreateDTO dto) {
        CatalogCreateResponseDTO response = catalogService.createCatalog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    //+
    @PostMapping("/{catalogId}/books")
    public ResponseEntity<CatalogAddBookResponseDTO> addBookToCatalog(
            @PathVariable Integer targetCatalogId,
            @RequestBody @Valid CatalogAddBookDTO dto) {
        if (!targetCatalogId.equals(dto.getCatalogId())) {
            throw new IllegalArgumentException("ID Каталога в  пути и в теле запроса" +
                    " должно совпадать " );
        }

        CatalogAddBookResponseDTO response = catalogService.addBookToCatalog(dto);
        return ResponseEntity.ok(response);
    }
    // +
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable Integer id) {
        catalogService.deleteCatalog(new RequestIdDTO(id));
        return ResponseEntity.noContent().build();
    }

    // переделать на удаление книги из каталога, плохой метод
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> removeBookFromAllCatalogs(@PathVariable Integer bookId) {
        catalogService.removeBookFromAllCatalogs(bookId);
        return ResponseEntity.noContent().build();
    }
    //+
    @GetMapping("/tree")
    public ResponseEntity<List<CatalogTreeDTO>> getCatalogTree() {
        List<CatalogTreeDTO> tree = catalogService.getCatalogTree();
        return ResponseEntity.ok(tree);
    }

    //метод показывающий книги в катологе тут?

}