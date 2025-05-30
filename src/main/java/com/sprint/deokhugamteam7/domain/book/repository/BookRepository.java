package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, UUID> {
  boolean existsByIsbnIsNotNullAndIsbn(String isbn);

  @Query("""
          SELECT b FROM Book b
          WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
             AND b.createdAt < :createdAt
      """)
  Slice<Book> findAllByKeyword(
      @Param("keyword") String keyword,
      @Param("createdAt") LocalDateTime createdAt,
      Pageable pageable);

  @Query("SELECT b FROM Book b WHERE b.id = :id AND b.isDeleted = false")
  Optional<Book> findByIdAndIsDeletedFalse(UUID id);

  boolean existsByIsbn(String isbn);
}
