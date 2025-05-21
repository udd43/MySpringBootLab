package com.rookies3.myspringbootlab.repository;

import com.rookies3.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 기본 조회
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    // 페이징/검색용
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Publisher 기준 조회
    List<Book> findByPublisherId(Long publisherId);
    Long countByPublisherId(@Param("publisherId") Long publisherId);

    // 상세정보 페치 조인
    @Query("SELECT b FROM Book b " +
            "LEFT JOIN FETCH b.bookDetail " +
            "WHERE b.id = :id")
    Optional<Book> findByIdWithBookDetail(@Param("id") Long id);

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN FETCH b.bookDetail " +
            "WHERE b.isbn = :isbn")
    Optional<Book> findByIsbnWithBookDetail(@Param("isbn") String isbn);

    @Query("SELECT b FROM Book b " +
            "LEFT JOIN FETCH b.bookDetail " +
            "LEFT JOIN FETCH b.publisher " +
            "WHERE b.id = :id")
    Optional<Book> findByIdWithAllDetails(@Param("id") Long id);
}
