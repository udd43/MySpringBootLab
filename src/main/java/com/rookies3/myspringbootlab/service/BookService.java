package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.entity.BookDetail;
import com.rookies3.myspringbootlab.entity.Publisher;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.exception.ErrorCode;
import com.rookies3.myspringbootlab.repository.BookDetailRepository;
import com.rookies3.myspringbootlab.repository.BookRepository;
import com.rookies3.myspringbootlab.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookDetailRepository bookDetailRepository;
    private final PublisherRepository publisherRepository;

    public List<BookDTO.Response> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    public BookDTO.Response getBookById(Long id) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", id));
        return BookDTO.Response.fromEntity(book);
    }

    public BookDTO.Response getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbnWithBookDetail(isbn)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "ISBN", isbn));
        return BookDTO.Response.fromEntity(book);
    }

    public List<BookDTO.Response> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    public List<BookDTO.Response> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    public List<BookDTO.Response> getBooksByPublisherId(Long publisherId) {
        publisherRepository.findById(publisherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Publisher", "id", publisherId));
        return bookRepository.findByPublisherId(publisherId)
                .stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    @Transactional
    public BookDTO.Response createBook(BookDTO.Request request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.ISBN_DUPLICATE, "ISBN", request.getIsbn());
        }
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Publisher", "id", request.getPublisherId()));
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publishDate(request.getPublishDate())
                .publisher(publisher)
                .build();
        if (request.getDetail() != null) {
            BookDetail detail = BookDetail.builder()
                    .description(request.getDetail().getDescription())
                    .language(request.getDetail().getLanguage())
                    .pageCount(request.getDetail().getPageCount())
                    .coverImageUrl(request.getDetail().getCoverImageUrl())
                    .edition(request.getDetail().getEdition())
                    .book(book)
                    .build();
            book.setBookDetail(detail);
        }
        Book saved = bookRepository.save(book);
        return BookDTO.Response.fromEntity(saved);
    }

    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request request) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", id));
        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.ISBN_DUPLICATE, "ISBN", request.getIsbn());
        }
        if (!book.getPublisher().getId().equals(request.getPublisherId())) {
            Publisher newPublisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Publisher", "id", request.getPublisherId()));
            book.setPublisher(newPublisher);
        }
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublishDate(request.getPublishDate());
        if (request.getDetail() != null) {
            BookDetail detail = book.getBookDetail();
            if (detail == null) {
                detail = new BookDetail();
                detail.setBook(book);
                book.setBookDetail(detail);
            }
            detail.setDescription(request.getDetail().getDescription());
            detail.setLanguage(request.getDetail().getLanguage());
            detail.setPageCount(request.getDetail().getPageCount());
            detail.setCoverImageUrl(request.getDetail().getCoverImageUrl());
            detail.setEdition(request.getDetail().getEdition());
        }
        Book updated = bookRepository.save(book);
        return BookDTO.Response.fromEntity(updated);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Book", "id", id);
        }
        bookRepository.deleteById(id);
    }
}
