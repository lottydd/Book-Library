package com.example.booklibrary.controller;

import com.example.booklibrary.model.Catalog;
import com.example.booklibrary.service.CatalogService;
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
    public ResponseEntity<Void> createCatalog(
            @RequestParam String name,
            @RequestParam(required = false) Integer parentId) {
        catalogService.createCatalog(name, parentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<Catalog>> getCatalogTree() {
        return ResponseEntity.ok(catalogService.getCatalogTree());
    }

    @PostMapping("/{catalogId}/books/{bookId}")
    public ResponseEntity<Void> addBookToCatalog(
            @PathVariable int catalogId,
            @PathVariable int bookId) {
        catalogService.addBookToCatalog(bookId, catalogId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{catalogId}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable int catalogId) {
        catalogService.deleteCatalog(catalogId);
        return ResponseEntity.noContent().build();
    }
}