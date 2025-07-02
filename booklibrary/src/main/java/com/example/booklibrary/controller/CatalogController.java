package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogBooksResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogTreeDTO;
import com.example.booklibrary.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Catalog", description = "Управление каталогами и книгами в них")
@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }


    @Operation(summary = "Создание нового каталога", description = "Создаёт новый каталог. Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CatalogCreateResponseDTO> createCatalog
            (@RequestBody @Valid CatalogCreateDTO dto) {
        CatalogCreateResponseDTO response = catalogService.createCatalog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Добавление книги в каталог", description = "Добавляет книгу в указанный каталог. Только для администратора")

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{targetCatalogId}/books")
    public ResponseEntity<CatalogAddBookResponseDTO> addBookToCatalog(
            @Parameter(description = "ID каталога") @PathVariable("targetCatalogId") Integer targetCatalogId,
            @RequestBody @Valid CatalogAddBookDTO dto) {
        if (!targetCatalogId.equals(dto.getCatalogId())) {
            throw new IllegalArgumentException("ID Каталога в  пути и в теле запроса" +
                    " должно совпадать ");
        }

        CatalogAddBookResponseDTO response = catalogService.addBookToCatalog(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удаление каталога", description = "Удаляет каталог по ID. Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalog(
            @Parameter(description = "ID удаляемого каталога") @PathVariable("id") Integer id) {
        catalogService.deleteCatalog(new RequestIdDTO(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удаление книги из каталога", description = "Удаляет книгу из указанного каталога. Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{catalogId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromCatalog(
            @Parameter(description = "ID каталога") @PathVariable("catalogId") Integer catalogId,
            @Parameter(description = "ID книги") @PathVariable("bookId") Integer bookId) {
        catalogService.removeBookFromCatalog(catalogId, bookId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получение дерева каталогов", description = "Возвращает иерархическое дерево всех каталогов. Доступно администратора и пользователя")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/tree")
    public ResponseEntity<List<CatalogTreeDTO>> getCatalogTree() {
        List<CatalogTreeDTO> tree = catalogService.getCatalogTree();
        return ResponseEntity.ok(tree);
    }

    @Operation(summary = "Получение книг из каталога", description = "Возвращает список книг в каталоге. Доступно администратора и пользователя")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{catalogId}/books")
    public ResponseEntity<List<CatalogBooksResponseDTO>> getBooksFromCatalog(
            @Parameter(description = "ID каталога") @PathVariable("catalogId") Integer catalogId) {
        List<CatalogBooksResponseDTO> catalogBooks = catalogService.getCatalogBooks(catalogId);
        return ResponseEntity.ok(catalogBooks);
    }


}