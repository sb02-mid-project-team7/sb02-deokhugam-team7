package com.sprint.deokhugamteam7.domain.book;

import com.sprint.deokhugamteam7.config.TestAuditingConfig;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.book.repository.RankingBookRepository;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class RankingBookRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private RankingBookRepository rankingBookRepository;

  private User user;
  private Book book;
  private Review review;

  @BeforeEach
  void setUp() {
    user = User.create("a@a", "a", "a");
    userRepository.save(user);
    book = Book.create("aaa", "bbb", "ccc", LocalDate.now()).build();
    bookRepository.save(book);
    review = Review.create(book, user, "test", 3);
    reviewRepository.save(review);
  }

  @Test
  void findPopularBooks() {
    // given

    // when

    // then

  }
}
