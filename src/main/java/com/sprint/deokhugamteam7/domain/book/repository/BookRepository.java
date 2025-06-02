package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, UUID> {

  boolean existsByIsbnIsNotNullAndIsbn(String isbn);

  @Query("SELECT b FROM Book b WHERE b.id = :id AND b.isDeleted = false")
  Optional<Book> findByIdAndIsDeletedFalse(UUID id);

  boolean existsByIsbn(String isbn);
}
