package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.entity.RankingBook;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RankingBookRepository extends JpaRepository<RankingBook, UUID> {

  @Query("""
          SELECT b FROM Book b
          WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND b.createdAt < :createdAt
      """)
  Slice<Book> findAllByKeyword(
      @Param("keyword") String keyword,
      @Param("createdAt") LocalDateTime createdAt,
      Pageable pageable);

  @Query("""
          SELECT b FROM RankingBook b
          WHERE b.period = :keyword AND b.book.isDeleted = false
      """)
  Slice<RankingBook> findPopularBooks(
      @Param("keyword") Period keyword, Pageable pageable);
}
