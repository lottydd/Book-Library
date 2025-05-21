package com.example.booklibrary.controller;

import com.example.booklibrary.dto.request.RequestIdDTO;
import com.example.booklibrary.dto.request.bookcopy.BookAddCopyDTO;
import com.example.booklibrary.dto.request.bookcopy.BookCopyUpdateDTO;
import com.example.booklibrary.dto.response.bookcopy.BookCopyDTO;
import com.example.booklibrary.service.BookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book Copies", description = "Управление копиями книг")
@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @Operation(summary = "Добавить копии книги", description = "Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping

    public ResponseEntity<Void> addCopies(@RequestBody @Valid BookAddCopyDTO dto) {
        bookCopyService.addCopies(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Удалить все копии книги по ID книги", description = "Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Void> deleteBookCopies(@PathVariable Integer bookId) {
        bookCopyService.deleteBookCopies(new RequestIdDTO(bookId));
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Обновить статус копии книги", description = "Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/status")
    public ResponseEntity<BookCopyDTO> updateCopyStatus(
            @RequestBody @Valid BookCopyUpdateDTO dto) {
        BookCopyDTO updatedCopy = bookCopyService.updateCopyStatus(dto);
        return ResponseEntity.ok(updatedCopy);
    }

    @Operation(summary = "Получить информацию об арендованных копиях книги по ID книги", description = "Только для администратора")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/has-rented/{bookId}")
    public ResponseEntity<List<BookCopyDTO>> rentedCopiesInfo(@PathVariable Integer bookId) {
        List<BookCopyDTO> rentedCopies = bookCopyService.getRentedCopies(new RequestIdDTO(bookId));
        return ResponseEntity.ok(rentedCopies);
    }

}