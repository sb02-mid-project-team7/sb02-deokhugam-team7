package com.sprint.deokhugamteam7.domain.comment.data;

import com.sprint.deokhugamteam7.domain.book.entity.Book;
import com.sprint.deokhugamteam7.domain.book.repository.BookRepository;
import com.sprint.deokhugamteam7.domain.comment.entity.Comment;
import com.sprint.deokhugamteam7.domain.review.entity.Review;
import com.sprint.deokhugamteam7.domain.review.repository.ReviewRepository;
import com.sprint.deokhugamteam7.domain.user.entity.User;
import com.sprint.deokhugamteam7.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
public class DataInitializer {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	TransactionTemplate transactionTemplate;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	BookRepository bookRepository; // Review 생성 시 Book이 필요하다면

	private User testUser;
	private Review testReview;

	CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

	static final int BULK_INSERT_SIZE = 600;
	static final int EXECUTE_COUNT = 200;

	@BeforeEach
	void setupTestData() {
		testUser = User.create("email", "nickname", "password");
		userRepository.saveAndFlush(testUser);

		Book testBook = Book.create("title", "author", "publisher", LocalDate.now())
			.title("title")
			.author("author")
			.description("description")
			.publisher("publisher")
			.publishedDate(LocalDate.now())
			.isbn("isbn")
			.thumbnailUrl("thumbnailUrl")
			.build();
		bookRepository.saveAndFlush(testBook);

		testReview = Review.create(testBook, testUser, "content", 1);
		reviewRepository.saveAndFlush(testReview);
	}

	// 데이터 삽입할때 application-test.yml 파일에 show-sql 설정을 잠깐 false 로 설정해야합니다.
	@Test
	@Commit
	void initialize() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (int cnt_i = 0; cnt_i < EXECUTE_COUNT; cnt_i++) {
			executorService.submit(() -> {
				insert();
				latch.countDown();
				log.info("latch.getCount(): {}", latch.getCount());
			});
		}
		latch.await();
		executorService.shutdown();
	}

	void insert() {
		transactionTemplate.executeWithoutResult(status -> {
			for (int cnt_i = 0; cnt_i < BULK_INSERT_SIZE; cnt_i++) {
				Comment comment = Comment.create(
					testUser,
					testReview,
					String.format("%d번째 test 댓글 입니다.", cnt_i)
				);
				entityManager.persist(comment);
			}
		});
	}
}
