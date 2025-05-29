package com.sprint.deokhugamteam7.domain.review.service;

import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import java.util.UUID;

public interface ReviewService {

  ReviewDto create(ReviewCreateRequest request);

  ReviewDto update(UUID id, UUID userId, ReviewUpdateRequest request);

  void deleteSoft(UUID id, UUID userId);

  void deleteHard(UUID id, UUID userId);
/*
  CursorPageResponseReviewDto findReviews(ReviewSearchCondition condition);
*/

}
