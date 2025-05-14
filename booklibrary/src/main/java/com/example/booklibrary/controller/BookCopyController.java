package com.example.booklibrary.controller;

import com.example.booklibrary.service.BookCopyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books/{bookId}/copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @PostMapping
    public ResponseEntity<Void> addCopies(
            @PathVariable int bookId,
            @RequestParam int count) {
        bookCopyService.addCopies(bookId, count);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllCopies(@PathVariable int bookId) {
        bookCopyService.deleteBookCopies(bookId);
        return ResponseEntity.noContent().build();
    }
}