package com.example.booklibrary.service;

import com.example.booklibrary.dao.BookCatalogDAO;
import com.example.booklibrary.dao.BookDAO;
import com.example.booklibrary.dao.CatalogDAO;
import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.catalog.CatalogAddBookDTO;
import com.example.booklibrary.dto.request.catalog.CatalogCreateDTO;
import com.example.booklibrary.dto.response.catalog.*;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.mapper.CatalogMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.model.BookCatalog;
import com.example.booklibrary.model.Catalog;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CatalogServiceTest {

    @Mock private CatalogDAO catalogDAO;
    @Mock private BookDAO bookDAO;
    @Mock private BookCatalogDAO bookCatalogDAO;
    @Mock private CatalogMapper catalogMapper;
    @Mock private BookMapper bookMapper;

    @InjectMocks private CatalogService catalogService;

    private Catalog catalog;
    private Book book;

    @BeforeEach
    void setup() {
        catalog = new Catalog();
        catalog.setId(1);
        catalog.setName("Fiction");

        book = new Book();
        book.setId(1);
    }

    @Test
    void createCatalog_positive() {
        CatalogCreateDTO dto = new CatalogCreateDTO("New Catalog", null);
        when(catalogDAO.save(any(Catalog.class))).thenAnswer(i -> i.getArgument(0));
        when(catalogMapper.toCatalogCreateResponseDTO(any())).thenReturn(new CatalogCreateResponseDTO(1, "New Catalog", null, "Created"));

        CatalogCreateResponseDTO response = catalogService.createCatalog(dto);

        assertEquals("New Catalog", response.getName());
        verify(catalogDAO).save(any());
    }

    @Test
    void createCatalog_negative_parentNotFound() {
        CatalogCreateDTO dto = new CatalogCreateDTO("SubCatalog", 999);
        when(catalogDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> catalogService.createCatalog(dto));
    }

    @Test
    void addBookToCatalog_positive() {
        CatalogAddBookDTO dto = new CatalogAddBookDTO(1, 1);

        when(bookDAO.findById(1)).thenReturn(Optional.of(book));
        when(catalogDAO.findById(1)).thenReturn(Optional.of(catalog));
        when(bookCatalogDAO.existsByBookAndCatalog(book, catalog)).thenReturn(false);

        BookCatalog bookCatalog = new BookCatalog();
        bookCatalog.setBook(book);
        bookCatalog.setCatalog(catalog);

        when(bookCatalogDAO.save(any())).thenReturn(bookCatalog);
        when(catalogMapper.toCatalogAddBookResponseDTO(bookCatalog)).thenReturn(new CatalogAddBookResponseDTO(1, "Fiction", 1, "Added"));

        CatalogAddBookResponseDTO response = catalogService.addBookToCatalog(dto);

        assertEquals(1, response.getCatalogId());
    }

    @Test
    void addBookToCatalog_negative_bookNotFound() {
        CatalogAddBookDTO dto = new CatalogAddBookDTO(1, 2);
        when(bookDAO.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> catalogService.addBookToCatalog(dto));
    }


    @Test
    void removeBookFromCatalog_positive() {
        when(bookCatalogDAO.existsByCatalogIdAndBookId(1, 1)).thenReturn(true);

        catalogService.removeBookFromCatalog(1, 1);

        verify(bookCatalogDAO).deleteByCatalogIdAndBookId(1, 1);
    }

    @Test
    void removeBookFromCatalog_negative() {
        when(bookCatalogDAO.existsByCatalogIdAndBookId(1, 2)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> catalogService.removeBookFromCatalog(1, 2));
    }

    @Test
    void removeBookFromAllCatalogs_positive() {
        when(bookDAO.existsById(1)).thenReturn(true);
        when(bookCatalogDAO.deleteByBookId(1)).thenReturn(2);

        assertDoesNotThrow(() -> catalogService.removeBookFromAllCatalogs(1));
    }

    @Test
    void removeBookFromAllCatalogs_negative() {
        when(bookDAO.existsById(2)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> catalogService.removeBookFromAllCatalogs(2));
    }

    @Test
    void deleteCatalog_positive() {
        RequestIdDTO dto = new RequestIdDTO(1);
        catalog.setChildren(Collections.emptyList());
        when(catalogDAO.findById(1)).thenReturn(Optional.of(catalog));

        catalogService.deleteCatalog(dto);

        verify(catalogDAO).delete(1);
    }

    @Test
    void deleteCatalog_negative_notFound() {
        RequestIdDTO dto = new RequestIdDTO(999);
        when(catalogDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> catalogService.deleteCatalog(dto));
    }

    @Test
    void getCatalogTree_positive() {
        when(catalogDAO.findRootCatalogs()).thenReturn(List.of(catalog));
        when(catalogDAO.findByParentId(anyInt())).thenReturn(Collections.emptyList());
        when(catalogMapper.toTreeDtoList(any())).thenReturn(List.of(new CatalogTreeDTO(1, "Fiction", List.of())));

        List<CatalogTreeDTO> result = catalogService.getCatalogTree();

        assertFalse(result.isEmpty());
    }

    @Test
    void getCatalogBooks_positive() {
        BookCatalog bc = new BookCatalog();
        bc.setBook(book);
        when(bookCatalogDAO.findByCatalogId(1)).thenReturn(List.of(bc));
        when(bookMapper.toCatalogBookResponseDTO(book)).thenReturn(new CatalogBooksResponseDTO("Title", "Author"));

        List<CatalogBooksResponseDTO> result = catalogService.getCatalogBooks(1);

        assertEquals(1, result.size());
    }
}
