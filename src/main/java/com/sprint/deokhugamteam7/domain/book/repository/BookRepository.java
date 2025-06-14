package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, UUID> {

  @Query("SELECT b FROM Book b JOIN FETCH b.rankingBooks WHERE b.id = :id AND b.isDeleted = false")
  Optional<Book> findByIdAndIsDeletedFalse(UUID id);

  Optional<Book> findByIsbn(String isbn);
}
