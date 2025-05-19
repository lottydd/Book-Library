package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogBooksResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogTreeDTO;
import com.example.booklibrary.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CatalogCreateResponseDTO> createCatalog
            (@RequestBody @Valid CatalogCreateDTO dto) {
        CatalogCreateResponseDTO response = catalogService.createCatalog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{targetCatalogId}/books")
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable Integer id) {
        catalogService.deleteCatalog(new RequestIdDTO(id));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{catalogId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromCatalog(@PathVariable Integer catalogId,
                                                      @PathVariable Integer bookId) {
        catalogService.removeBookFromCatalog(catalogId, bookId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/tree")
    public ResponseEntity<List<CatalogTreeDTO>> getCatalogTree() {
        List<CatalogTreeDTO> tree = catalogService.getCatalogTree();
        return ResponseEntity.ok(tree);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{catalogId}/books")
    public ResponseEntity<List<CatalogBooksResponseDTO>> getBooksFromCatalog(@PathVariable Integer catalogId){
        List<CatalogBooksResponseDTO> catalogBooks = catalogService.getCatalogBooks(catalogId);
        return ResponseEntity.ok(catalogBooks);
    }


}