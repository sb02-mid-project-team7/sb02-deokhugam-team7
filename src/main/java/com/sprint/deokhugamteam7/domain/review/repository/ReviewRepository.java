package com.sprint.deokhugamteam7.domain.review.repository;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

}
