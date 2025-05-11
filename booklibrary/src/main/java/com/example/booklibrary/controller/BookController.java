package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.book.BookCreateDTO;
import com.example.booklibrary.dto.request.book.BookDetailsDTO;
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


    @PostMapping
    public ResponseEntity<BookResponseDTO> addBook(@RequestBody @Valid BookCreateDTO dto) {
        return new ResponseEntity<>(bookService.createBook(dto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<BookResponseDTO> updateBook(@RequestBody @Valid BookUpdateDTO dto) {
        return ResponseEntity.ok(bookService.updateBook(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDTO> getBookDetails(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookDetails(id));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}