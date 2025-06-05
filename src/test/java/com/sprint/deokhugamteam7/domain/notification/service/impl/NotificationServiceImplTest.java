package com.sprint.deokhugamteam7.domain.notification.service.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationUpdateRequest;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.repository.NotificationRepository;
import com.sprint.deokhugamteam7.domain.notification.service.NotificationServiceImpl;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.notification.NotificationException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    private final UUID NOTIFICATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID OTHER_USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private User user;
    private User otherUser;
    private Review review;
    private Notification notification;
    private NotificationUpdateRequest request;

    @BeforeEach
    void setup() {
        user = User.create("test1", "test1", "test1");
        setPrivateField(user, "id", USER_ID);

        otherUser = User.create("test2", "test2", "test2");
        setPrivateField(otherUser, "id", OTHER_USER_ID);

        Book book = Book.create("testBook", "testBook", "testBook", LocalDate.now()).build();

        review = Review.create(book, user, "책의 리뷰입니다.", 3);
        setPrivateField(review, "user", user);

        notification = Notification.create(user, review, "테스트 알림");
        setPrivateField(notification, "id", NOTIFICATION_ID);

        request = new NotificationUpdateRequest(true);
    }

    @Test
    @DisplayName("알림 수정 성공 - confirmed 수정 확인")
    void 알림_수정_성공() {
        // given
        given(notificationRepository.findById(any())).willReturn(Optional.of(notification));
        given(userRepository.findById(any())).willReturn(Optional.of(user));

        // when
        NotificationDto result = notificationService.update(NOTIFICATION_ID, USER_ID, request);

        // then
        assertThat(result.confirmed()).isTrue();
    }

    @Test
    @DisplayName(("알림 수정 실패 - NOTIFICATION_NOT_FOUND"))
    void notificationNotFoundById() {
        // given
        given(notificationRepository.findById(notification.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.update(notification.getId(), user.getId(), request))
            .isInstanceOf(NotificationException.class)
            .hasMessageContaining(ErrorCode.NOTIFICATION_NOT_FOUND.getMessage());
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