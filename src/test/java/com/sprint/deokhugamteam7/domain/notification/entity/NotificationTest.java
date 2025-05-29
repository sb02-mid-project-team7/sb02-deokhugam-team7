package com.sprint.deokhugamteam7.domain.notification.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.notification.NotificationException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationTest {

    private final UUID NOTIFICATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID OTHER_USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private User user;
    private User otherUser;
    private Review review;
    private Notification notification;

    @BeforeEach
    void setup() {
        user = User.create("test1", "test1", "test1");
        setPrivateField(user, "id", USER_ID);

        otherUser = User.create("test2", "test2", "test2");
        setPrivateField(otherUser, "id", OTHER_USER_ID);

        review = new Review();
        setPrivateField(review, "user", user);

        notification = Notification.create(user, review, "테스트 알림");
        setPrivateField(notification, "id", NOTIFICATION_ID);
    }

    @Test
    @DisplayName("알림 접근 권한 검사 성공 - 작성자와 요청자 일치")
    void validateUserAuthorization_success() {
        assertDoesNotThrow(() -> notification.validateUserAuthorization(USER_ID));
    }

    @Test
    @DisplayName("알림 접근 권한 검사 실패 - 작성자와 요청자 불일치")
    void validateUserAuthorization_fail() {
        assertThatThrownBy(() -> notification.validateUserAuthorization(OTHER_USER_ID))
            .isInstanceOf(NotificationException.class)
            .hasMessageContaining(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("리플렉션으로 필드 설정 실패: " + fieldName, e);
        }
    }

}