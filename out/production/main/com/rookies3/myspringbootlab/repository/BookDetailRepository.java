package com.rookies3.myspringbootlab.repository;

import com.rookies3.myspringbootlab.entity.BookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookDetailRepository extends JpaRepository<BookDetail, Long> {

    /**
     * 특정 Book의 상세정보 조회
     */
    Optional<BookDetail> findByBookId(Long bookId);

    /**
     * Book을 페치 조인해서 상세정보와 함께 한 번에 가져오기
     */
    @Query("SELECT bd FROM BookDetail bd JOIN FETCH bd.book WHERE bd.id = :id")
    Optional<BookDetail> findByIdWithBook(@Param("id") Long id);

    /**
     * 출판사 ID로 해당 출판사의 모든 BookDetail 목록 조회
     */
    @Query("""
           SELECT bd
             FROM BookDetail bd
             JOIN bd.book b
            WHERE b.publisher.id = :publisherId
           """)
    List<BookDetail> findByPublisherId(@Param("publisherId") Long publisherId);
}
