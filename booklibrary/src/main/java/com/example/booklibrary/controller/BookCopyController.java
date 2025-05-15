package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.bookcopy.BookAddCopyDTO;
import com.example.booklibrary.dto.request.bookcopy.BookCopyUpdateDTO;
import com.example.booklibrary.dto.response.bookcopy.BookCopyDTO;
import com.example.booklibrary.service.BookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    //+
    @PostMapping
    public ResponseEntity<Void> addCopies(@RequestBody @Valid BookAddCopyDTO dto) {
        bookCopyService.addCopies(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //?
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Void> deleteBookCopies(@PathVariable Integer bookId) {
        bookCopyService.deleteBookCopies(new RequestIdDTO(bookId));
        return ResponseEntity.noContent().build();
    }

    //+
    @PatchMapping("/status")
    public ResponseEntity<BookCopyDTO> updateCopyStatus(
            @RequestBody @Valid BookCopyUpdateDTO dto) {
        BookCopyDTO updatedCopy = bookCopyService.updateCopyStatus(dto);
        return ResponseEntity.ok(updatedCopy);
    }

    //Переделать сервисный чтобы возвращало информацию об арендованных копиях но не точно
    @GetMapping("/has-rented/{bookId}")
    public ResponseEntity<Boolean> hasRentedCopies(@PathVariable Integer bookId) {
        boolean hasRented = bookCopyService.hasRentedCopies(new RequestIdDTO(bookId));
        return ResponseEntity.ok(hasRented);
    }

}