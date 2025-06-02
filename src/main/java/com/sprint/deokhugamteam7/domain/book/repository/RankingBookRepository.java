package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.constant.Period;
import com.sprint.deokhugamteam7.domain.book.dto.FindBookDto;
import com.sprint.deokhugamteam7.domain.book.dto.FindPopularBookDto;
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
            SELECT new com.sprint.deokhugamteam7.domain.book.dto.FindBookDto(
                b.id, b.title, b.author, b.description, b.publisher,
                b.publishedDate, b.isbn, b.thumbnailUrl,
                rb.reviewCount, rb.rating,
                b.createdAt, b.updatedAt
            )
          FROM RankingBook rb
          JOIN rb.book b
          WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND b.createdAt < :createdAt AND rb.period = "ALL_TIME" AND b.isDeleted = false
      """)
  Slice<FindBookDto> findAllByKeyword(
      @Param("keyword") String keyword,
      @Param("createdAt") LocalDateTime createdAt,
      Pageable pageable);

  @Query("""
          SELECT new com.sprint.deokhugamteam7.domain.book.dto.FindPopularBookDto(
                rb.id,
                b.id,
                b.createdAt,
                b.title,
                b.author,
                b.thumbnailUrl,
                CAST(rb.period as string),
                rb.score,
                rb.reviewCount,
                rb.rating
                ) FROM RankingBook rb
          JOIN rb.book b
          WHERE rb.period = :keyword AND b.isDeleted = false
      """)
  Slice<FindPopularBookDto> findPopularBooks(
      @Param("keyword") Period keyword, Pageable pageable);
}
