package com.sprint.deokhugamteam7.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugamteam7.config.QueryDslConfig;
import com.sprint.deokhugamteam7.config.TestAuditingConfig;
import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QueryDslConfig.class, TestAuditingConfig.class})
class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	private User testUser;
	private Review testReview;

	@BeforeEach
	void setUp() {
		testUser = User.create("test@user.com", "testuser", "password");
		testEntityManager.persist(testUser);

		Book testBook = Book.create("Test Book", "Test Author", "Test Publisher", LocalDate.now())
			.description("이것은 테스트용 책입니다.") // Builder를 통해 다른 필드들도 설정 가능
			.isbn("978-1234567890")
			.thumbnailUrl("http://example.com/image.jpg")
			.build();
		testEntityManager.persist(testBook);

		testReview = Review.create(testBook, testUser, "Test Review", 5);
		testEntityManager.persist(testReview);

		for (int i = 0; i < 10; i++) {
			Comment comment = Comment.create(testUser, testReview, "Test Comment " + i);
			testEntityManager.persist(comment);
		}
		testEntityManager.flush();
		testEntityManager.clear();
	}

	@Test
	@DisplayName("첫 페이지 조회 (findFirstPage) 테스트")
	void findFirstPageTest() {
		// given
		int limit = 5;

		// when
		List<Comment> firstPage = commentRepository.findFirstPage(testReview.getId(), "DESC",
			limit);

		// then
		assertThat(firstPage).hasSize(limit);
	}

	@Test
	@DisplayName("다음 페이지 조회 (findNextPage) 테스트")
	void findNextPage_Success() {
		// given
		int firstPageLimit = 3;
		List<Comment> firstPage = commentRepository.findFirstPage(testReview.getId(), "DESC",
			firstPageLimit);
		Comment lastCommentOfFirstPage = firstPage.get(firstPage.size() - 1);
		UUID cursorId = lastCommentOfFirstPage.getId();
		LocalDateTime createdAt = lastCommentOfFirstPage.getCreatedAt();

		int nextPageLimit = 3;

		// when
		List<Comment> nextPage = commentRepository.findNextPage(testReview.getId(), "DESC",
			cursorId, createdAt, nextPageLimit);

		// then
		assertThat(nextPage).hasSize(nextPageLimit);
		assertThat(nextPage.get(0).getCreatedAt()).isBeforeOrEqualTo(createdAt);
	}

	@Test
	@DisplayName("리뷰 ID로 댓글 수 조회 (countByReviewId) 테스트")
	void countByReviewId_Success() {
		// given
		// @BeforeEach에서 총 10개의 댓글이 생성됨

		// when
		Long totalCount = commentRepository.countByReviewId(testReview.getId());

		// then
		assertThat(totalCount).isEqualTo(10L);
	}
}
