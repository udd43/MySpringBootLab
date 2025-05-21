package com.rookies3.myspringbootlab.controller;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.exception.ErrorCode;
import com.rookies3.myspringbootlab.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO.Response>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO.Response> getBookById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookService.getBookById(id));
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw ex;
        }
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO.Response> getBookByIsbn(@PathVariable String isbn) {
        try {
            return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw ex;
        }
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO.Response>> getBooksByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(author));
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO.Response>> getBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<List<BookDTO.Response>> getBooksByPublisher(@PathVariable Long publisherId) {
        try {
            return ResponseEntity.ok(bookService.getBooksByPublisherId(publisherId));
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw ex;
        }
    }

    @PostMapping
    public ResponseEntity<BookDTO.Response> createBook(@Valid @RequestBody BookDTO.Request request) {
        BookDTO.Response created = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO.Response> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO.Request request) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, request));
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCode.RESOURCE_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (ex.getErrorCode() == ErrorCode.ILLEGAL_STATE) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            throw ex;
        }
    }
}
