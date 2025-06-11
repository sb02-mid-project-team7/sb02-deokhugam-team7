package com.sprint.deokhugamteam7.constant;

import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.user.entity.User;

public enum NotificationType {
    LIKE("[%s]님이 나의 리뷰를 좋아합니다."),
    COMMENT("[%s]님이 나의 리뷰에 댓글을 남겼습니다.\n%s");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String formatMessage(User user, Comment comment) {
        return switch (this) {
            case LIKE -> String.format(message, user.getNickname());
            case COMMENT -> String.format(message, user.getNickname(), comment.getContent());
        };
    }
}