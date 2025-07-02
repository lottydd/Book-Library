package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.book.BookCreateDTO;
import com.example.booklibrary.dto.response.book.BookDetailsDTO;
import com.example.booklibrary.dto.request.book.BookUpdateDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Books", description = "Управление книгами")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Создать новую книгу", description = "Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookCreateDTO bookCreateDTO) {
        BookResponseDTO response = bookService.createBook(bookCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Обновить книгу", description = "Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<BookResponseDTO> updateBook(@Valid @RequestBody BookUpdateDTO bookUpdateDTO) {
        BookResponseDTO response = bookService.updateBook(bookUpdateDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить книгу", description = "Удаление по ID (только для администратора)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") int id) {
        bookService.deleteBook(new RequestIdDTO(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить информацию о книге", description = "Доступно всем авторизованным пользователям")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDTO> getBookDetails(@PathVariable("id") int id) {
        BookDetailsDTO response = bookService.getBookDetails(new RequestIdDTO(id));
        return ResponseEntity.ok(response);
    }

}