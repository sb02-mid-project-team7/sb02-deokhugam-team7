package com.sprint.deokhugamteam7.domain.book.repository;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, UUID> {

}
