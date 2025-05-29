package com.sprint.deokhugamteam7.domain.review.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.review.dto.request.ReviewCreateRequest;
import com.sprint.deokhugamteam7.domain.review.dto.response.ReviewDto;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
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
public class BasicReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private BasicReviewService reviewService;

  private UUID bookId;
  private UUID userId;

  private User user;
  private Book book;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    userId = UUID.randomUUID();

    user = User.create("test@gmail.com", "test", "test1234!");
    book = Book.create(
        "도메인 주도 설계", "에릭 에반스", "한빛미디어",
        LocalDate.of(2020, 1, 15)
    ).build();

    when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findByIdAndIsDeletedFalse(bookId)).thenReturn(Optional.of(book));
  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview() {
    // given
    ReviewCreateRequest request
        = new ReviewCreateRequest(bookId, userId, "책의 리뷰입니다.", 3);

    // when
    ReviewDto result = reviewService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("책의 리뷰입니다.");
    assertThat(result.rating()).isEqualTo(3);
  }
}
