package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.bookcopy.BookCopyDTO;
import com.example.booklibrary.service.BookCopyService;
import com.example.booklibrary.util.CopyStatus;
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

    @PatchMapping("/{copyId}/status")
    public ResponseEntity<BookCopyDTO> updateCopyStatus(
            @PathVariable int copyId,
            @RequestParam CopyStatus status) {
        return ResponseEntity.ok(bookCopyService.updateCopyStatus(copyId, status));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllCopies(@PathVariable int bookId) {
        bookCopyService.deleteAllCopiesForBook(bookId);
        return ResponseEntity.noContent().build();
    }
}