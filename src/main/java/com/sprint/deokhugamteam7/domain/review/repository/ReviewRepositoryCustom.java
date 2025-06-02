package com.sprint.deokhugamteam7.domain.review.repository;

import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewSearchCondition;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import java.util.List;

public interface ReviewRepositoryCustom {

  List<Review> findAll(ReviewSearchCondition condition, int limit);

  long countByCondition(ReviewSearchCondition condition);
}
