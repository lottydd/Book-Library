package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.CatalogAddBookResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogBooksResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogCreateResponseDTO;
import com.example.booklibrary.dto.response.catalog.CatalogTreeDTO;
import com.example.booklibrary.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class CatalogControllerTest {

    @Mock
    private CatalogService catalogService;

    @InjectMocks
    private CatalogController catalogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCatalog_Positive() {
        CatalogCreateDTO dto = new CatalogCreateDTO();
        CatalogCreateResponseDTO response = new CatalogCreateResponseDTO();
        when(catalogService.createCatalog(dto)).thenReturn(response);

        ResponseEntity<CatalogCreateResponseDTO> result = catalogController.createCatalog(dto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void addBookToCatalog_Positive() {
        int id = 1;
        CatalogAddBookDTO dto = new CatalogAddBookDTO();
        dto.setCatalogId(id);
        CatalogAddBookResponseDTO response = new CatalogAddBookResponseDTO();
        when(catalogService.addBookToCatalog(dto)).thenReturn(response);

        ResponseEntity<CatalogAddBookResponseDTO> result = catalogController.addBookToCatalog(id, dto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void addBookToCatalog_Negative_DifferentIds() {
        int pathId = 1;
        int bodyId = 2;
        CatalogAddBookDTO dto = new CatalogAddBookDTO();
        dto.setCatalogId(bodyId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            catalogController.addBookToCatalog(pathId, dto);
        });

        assertTrue(exception.getMessage().contains("должно совпадать"));
    }

     @Test
     void deleteCatalog_Positive() {
         int id = 1;

         ResponseEntity<Void> result = catalogController.deleteCatalog(id);

         verify(catalogService).deleteCatalog(refEq(new RequestIdDTO(id)));
         assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
     }
    @Test
    void removeBookFromCatalog_Positive() {
        int catalogId = 1, bookId = 2;

        ResponseEntity<Void> result = catalogController.removeBookFromCatalog(catalogId, bookId);

        verify(catalogService).removeBookFromCatalog(catalogId, bookId);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void getCatalogTree_Positive() {
        List<CatalogTreeDTO> tree = List.of(new CatalogTreeDTO());
        when(catalogService.getCatalogTree()).thenReturn(tree);

        ResponseEntity<List<CatalogTreeDTO>> result = catalogController.getCatalogTree();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(tree, result.getBody());
    }

    @Test
    void getBooksFromCatalog_Positive() {
        int catalogId = 1;
        List<CatalogBooksResponseDTO> books = List.of(new CatalogBooksResponseDTO());
        when(catalogService.getCatalogBooks(catalogId)).thenReturn(books);

        ResponseEntity<List<CatalogBooksResponseDTO>> result = catalogController.getBooksFromCatalog(catalogId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(books, result.getBody());
    }
}