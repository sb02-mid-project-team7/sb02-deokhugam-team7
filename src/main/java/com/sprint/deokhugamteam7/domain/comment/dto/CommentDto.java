package com.sprint.deokhugamteam7.domain.comment.dto;

import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
    UUID id,
    UUID reviewId,
    UUID userId,
    String userNickname,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static CommentDto from(Comment comment) {
        User user = comment.getUser();
        Review review = comment.getReview();

        return new CommentDto(
            comment.getId(),
            review.getId(),
            user.getId(),
            user.getNickname(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }
}
