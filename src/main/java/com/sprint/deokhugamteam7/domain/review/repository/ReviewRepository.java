package com.sprint.deokhugamteam7.domain.review.repository;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

  @Query("SELECT r FROM Review r "
      + "JOIN FETCH r.user "
      + "JOIN FETCH r.book"
      + " WHERE r.id = :id")
  Optional<Review> findByIdWithUserAndBook(@Param("id") UUID id);
}
