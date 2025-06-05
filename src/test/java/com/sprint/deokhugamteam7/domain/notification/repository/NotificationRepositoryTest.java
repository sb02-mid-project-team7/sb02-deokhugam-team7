package com.sprint.deokhugamteam7.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.notification.config.TestConfig;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationCursorRequest;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@Transactional
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager em;

    private User user;
    private Review review;
    private Book book;

    @BeforeEach
    void setUp() {
        // 유저 생성
        user = User.create("test1", "test1", "test1");
        book = Book.create("test", "test", "???", LocalDate.now()).build();

        em.persist(user);
        em.persist(book);

        // 리뷰 생성
        review = Review.create(book, user, "test", 5);
        em.persist(review);

        for (int i = 0; i < 3; i++) {
            Notification n = Notification.create(user, review, "알림 내용 " + i);
            em.persist(n);
        }

        em.flush();
        em.clear();
    }

    @Test
    void findAllByUserId_성공() {
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());

        assertThat(notifications).hasSize(3);
        assertThat(notifications).allMatch(n -> n.getUser().getId().equals(user.getId()));
    }

    @Test
    void bulkUpdateConfirmed_성공() {

        List<Notification> beforeUpdate = notificationRepository.findByUserId(user.getId());
        assertThat(beforeUpdate).allMatch(n -> !n.getConfirmed());

        notificationRepository.bulkUpdateConfirmed(user.getId());
        em.flush();
        em.clear();

        List<Notification> afterUpdate = notificationRepository.findByUserId(user.getId());
        assertThat(afterUpdate).allMatch(Notification::getConfirmed);
    }

    @Test
    void softDeleteNotificationsOlderThanNow() {
        assertThat(notificationRepository.findByUserId(user.getId())).hasSize(3);

        notificationRepository.softDeleteOldNotifications(LocalDateTime.now());

        assertThat(notificationRepository.findByUserId(user.getId())).isEmpty();
    }

    @Test
    void 커서_기반_목록_조회_desc() {
        NotificationCursorRequest request = new NotificationCursorRequest(
            user.getId(),
            "DESC",
            null,
            null,
            2
        );

        Slice<NotificationDto> result = notificationRepository.findAllByCursor(request);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent().get(0).content()).startsWith("알림 내용");
    }

}