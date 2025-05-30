package com.sprint.deokhugamteam7.domain.review.controller;

import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewUpdateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewLikeDto;
import com.sprint.deokhugamteam7.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<ReviewDto> create(@RequestBody @Valid ReviewCreateRequest request) {
    ReviewDto reviewDto = reviewService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(reviewDto);
  }

  @PatchMapping(path = "/{reviewId}")
  public ResponseEntity<ReviewDto> update(
      @PathVariable() UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId,
      @RequestBody @Valid ReviewUpdateRequest request) {
    ReviewDto reviewDto = reviewService.update(reviewId, userId, request);
    return ResponseEntity.ok(reviewDto);
  }

  @DeleteMapping(path = "/{reviewId}")
  public ResponseEntity<Void> deleteSoft(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId) {
    reviewService.deleteSoft(reviewId, userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(path = "/{reviewId}/hard")
  public ResponseEntity<Void> deleteHard(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId) {
    reviewService.deleteHard(reviewId, userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(path = "/{reviewId}")
  public ResponseEntity<ReviewDto> findById(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId) {
    ReviewDto reviewDto = reviewService.findById(reviewId, userId);
    return ResponseEntity.ok(reviewDto);
  }

  @PostMapping(path = "/{reviewId}/like")
  public ResponseEntity<ReviewLikeDto> like(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID") UUID userId) {
    ReviewLikeDto reviewLikeDto = reviewService.like(reviewId, userId);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(reviewLikeDto);
  }
}
