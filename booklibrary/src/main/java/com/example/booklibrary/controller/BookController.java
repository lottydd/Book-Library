package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.book.BookCreateDTO;
import com.example.booklibrary.dto.response.book.BookDetailsDTO;
import com.example.booklibrary.dto.request.book.BookUpdateDTO;
import com.example.booklibrary.dto.response.book.BookResponseDTO;
import com.example.booklibrary.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //+
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookCreateDTO bookCreateDTO) {
        BookResponseDTO response = bookService.createBook(bookCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //+
    @PutMapping
    public ResponseEntity<BookResponseDTO> updateBook(@Valid @RequestBody BookUpdateDTO bookUpdateDTO) {
        BookResponseDTO response = bookService.updateBook(bookUpdateDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        bookService.deleteBook(new RequestIdDTO(id));
        return ResponseEntity.noContent().build();
    }
    //+
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDTO> getBookDetails(@PathVariable int id) {
        BookDetailsDTO response = bookService.getBookDetails(new RequestIdDTO(id));
        return ResponseEntity.ok(response);
    }

}