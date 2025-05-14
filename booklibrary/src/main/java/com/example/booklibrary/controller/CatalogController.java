package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.book.BookUpdateDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
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

    @PostMapping
    public ResponseEntity<CatalogCreateResponseDTO> createCatalog(
            @RequestBody @Valid CatalogCreateDTO dto) {
        CatalogCreateResponseDTO response = catalogService.createCatalog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<Catalog>> getCatalogTree() {
        return ResponseEntity.ok(catalogService.getCatalogTree());
    }

    @PostMapping("/{catalogId}/books")
    public ResponseEntity<CatalogAddBookResponseDTO> addBookToCatalog(
            @PathVariable Integer catalogId,
            @RequestBody @Valid CatalogAddBookDTO dto) {
        if (!catalogId.equals(dto.getCatalogId())) {
            throw new IllegalArgumentException("Catalog ID in path and body must match");
        }

        CatalogAddBookResponseDTO response = catalogService.addBookToCatalog(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{catalogId}")
    public ResponseEntity<?> deleteCatalog(@PathVariable int catalogId) {
        try {
            catalogService.deleteCatalog(catalogId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}