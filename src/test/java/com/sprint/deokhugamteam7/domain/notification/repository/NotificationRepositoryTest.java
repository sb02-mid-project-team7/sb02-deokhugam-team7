package com.sprint.deokhugamteam7.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.config.AppConfig;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(value = AppConfig.class)
@Transactional
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager em;

    private User user;
    private User reviewer;
    private Review review;
    private Book book;

    @BeforeEach
    void setUp() {
        // 유저 생성
        user = User.create("test1", "test1", "test1");
        reviewer = User.create("test2", "test2", "test2");
        book = Book.create("test", "test", "???", LocalDate.now()).build();

        em.persist(user);
        em.persist(reviewer);
        em.persist(book);

        // 리뷰 생성
        review = Review.create(book, reviewer, "test", 5);
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
        List<Notification> notifications = notificationRepository.findAllByReviewerId(reviewer.getId());

        assertThat(notifications).hasSize(3);
        assertThat(notifications).allMatch(n -> n.getReview().getUser().getId().equals(reviewer.getId()));
    }

    @Test
    void bulkUpdateConfirmed_성공() {

        List<Notification> beforeUpdate = notificationRepository.findAllByReviewerId(reviewer.getId());
        assertThat(beforeUpdate).allMatch(n -> !n.getConfirmed());

        notificationRepository.bulkUpdateConfirmed(reviewer.getId());
        em.flush();
        em.clear();

        List<Notification> afterUpdate = notificationRepository.findAllByReviewerId(reviewer.getId());
        assertThat(afterUpdate).allMatch(Notification::getConfirmed);
    }
}