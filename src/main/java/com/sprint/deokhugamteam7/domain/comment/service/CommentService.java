package com.sprint.deokhugamteam7.domain.comment.service;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public CommentDto create(CommentCreateRequest commentCreateRequest) {
        UUID userId = commentCreateRequest.userId();
        UUID reviewId = commentCreateRequest.reviewId();

        User user = userRepository.findById(userId).orElseThrow();
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        String content = commentCreateRequest.content();

        Comment newComment = Comment.create(
            user,
            review,
            content
        );

        Comment savedComment = commentRepository.save(newComment);
        return new CommentDto(
            savedComment.getId(),
            savedComment.getReview().getId(),
            savedComment.getUser().getId(),
            savedComment.getUser().getNickname(),
            savedComment.getContent(),
            savedComment.getCreatedAt(),
            savedComment.getUpdatedAt()
        );
    }

    public CommentDto update(CommentUpdateRequest commentUpdateRequest) {
        log.debug("메시지 수정 시작: request={}", commentUpdateRequest);
        return null;
    }

    public void deleteHard(UUID commentId, UUID userId) {

    }

    public void deleteSoft(UUID commentId, UUID userId) {

    }


    public CursorPageResponseCommentDto getCommentList(UUID reviewId, String direction,
        String cursor,
        LocalDateTime after, int limit) {

        return null;
    }
}
