package com.sprint.deokhugamteam7.domain.review.service.basic;

import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicReviewService {

  private ReviewRepository reviewRepository;

}
